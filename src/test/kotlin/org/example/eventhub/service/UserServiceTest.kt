package org.example.eventhub.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.dto.user.UserFilter
import org.example.eventhub.dto.user.UserResponseLong
import org.example.eventhub.dto.user.UserResponseShort
import org.example.eventhub.dto.user.UserUpdateRequest
import org.example.eventhub.mapper.UserMapper
import org.example.eventhub.repository.UserRepository
import org.example.eventhub.specification.UserSpecification
import org.example.eventhub.entity.User
import org.example.eventhub.enums.Role
import org.example.eventhub.exception.UserAlreadyExistsException
import org.example.eventhub.exception.UserNotFoundException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.Optional

class UserServiceTest : StringSpec({

    val repository = mockk<UserRepository>()
    val mapper = mockk<UserMapper>()
    val specification = mockk<UserSpecification>()
    val passwordEncoder = mockk<PasswordEncoder>()

    val userService = UserService(repository, mapper, specification, passwordEncoder)

    val user = User(
        id = 1,
        username = "username",
        email = "email@email.com",
        password = "password",
        role = Role.USER
    )
    val userResponseLong = UserResponseLong(
        id = 1,
        username = "username",
        email = "email@email.com",
        role = Role.USER,
        createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 0),
        countOrganizedEvents = 0
    )
    val userResponseShort = UserResponseShort(
        username = "username",
    )
    val filter = UserFilter(null, null, null, null, null, null)
    val page = PageRequest.of(0, 10)


    every { mapper.toLongDto(user) } returns userResponseLong
    every { mapper.toShortDto(user) } returns userResponseShort

    every { passwordEncoder.encode(any()) } returns "password"

    every { repository.findByUsername("notExistingUsername") } returns Optional.empty()
    every { repository.findByEmail("notExisting@email.com") } returns Optional.empty()
    every { repository.findById(-1) } returns Optional.empty()

    every { repository.findByUsername("existingUsername") } returns Optional<User>.of(user)
    every { repository.findByEmail("existing@email.com") } returns Optional<User>.of(user)
    every { repository.findById(1) } returns Optional<User>.of(user)

    every { repository.save(any<User>()) } returns user


    //----------------createUser-----------------

    "createUser сохраняет пользователя в БД и возвращает UserResponseLong" {
        val dto = RegisterRequest(
            username = "notExistingUsername",
            email = "notExisting@email.com",
            password = "password"
        )
        every { mapper.toEntity(dto, "password") } returns user

        val response = userService.createUser(dto)

        response shouldBe userResponseLong

        verify(exactly = 1) { passwordEncoder.encode(user.password) }
        verify(exactly = 1) { repository.findByUsername("notExistingUsername") }
        verify(exactly = 1) { repository.findByEmail("notExisting@email.com") }
        verify(exactly = 1) { repository.save(user) }
    }

    "createUser выбрасывает исключение, если пользователь с таким же username существует" {
        val dto = RegisterRequest(
            username = "existingUsername",
            email = "notExisting@email.com",
            password = "password"
        )

        val ex = shouldThrow<UserAlreadyExistsException> {
            userService.createUser(dto)
        }

        ex.message shouldBe "Пользователь с username existingUsername уже существует"
    }

    "createUser выбрасывает исключение, если пользователь с таким же email существует" {
        val dto = RegisterRequest(
            username = "notExistingUsername",
            email = "existing@email.com",
            password = "password"
        )

        val ex = shouldThrow<UserAlreadyExistsException> {
            userService.createUser(dto)
        }

        ex.message shouldBe "Пользователь с email existing@email.com уже существует"
    }

    //----------------getAllUsers-----------------

    "getAllUsers возвращает список всех пользователей, если нет фильтра" {
        val userSpec = mockk<Specification<User>>()

        every { specification.withFilter(filter) } returns userSpec
        every { repository.findAll(userSpec, page) } returns PageImpl(listOf(user))

        val result = userService.getAllUsers(page, filter)

        result.content shouldBe listOf(userResponseShort)
    }

    //----------------getUserById-----------------

    "getUserById возвращает UserResponseLong, если пользователь есть" {
        val result = userService.getUserById(1)
        result shouldBe userResponseLong
    }

    "getUserById выбрасывает ошибку, если пользователя не существует" {
        val ex = shouldThrow<UserNotFoundException> {
            userService.getUserById(-1)
        }

        ex.message shouldBe "Пользователь с id -1 не найден"
    }

    //----------------getUserByUsername-----------------

    "getUserByUsername возвращает UserResponseLong, если пользователь есть" {
        val result = userService.getUserByUsername("existingUsername")
        result shouldBe userResponseLong
    }

    "getUserByUsername выбрасывает ошибку, если пользователя не существует" {
        val ex = shouldThrow<UserNotFoundException> {
            userService.getUserByUsername("notExistingUsername")
        }

        ex.message shouldBe "Пользователь с username notExistingUsername не найден"
    }

    //----------------updateUser-----------------

    "updateUser обновляет username, email и password" {
        val userDetails = mockk<org.springframework.security.core.userdetails.UserDetails>()
        every { userDetails.username } returns "email@email.com"

        val dto = UserUpdateRequest(
            username = "newUsername",
            email = "new@email.com",
            password = "newPassword"
        )

        every { repository.findByEmail("email@email.com") } returns Optional.of(user)
        every { repository.findByUsername("newUsername") } returns Optional.empty()
        every { repository.findByEmail("new@email.com") } returns Optional.empty()
        every { passwordEncoder.encode("newPassword") } returns "encodedPassword"

        val result = userService.updateUser(userDetails, dto)

        result shouldBe userResponseLong

        user.username shouldBe "newUsername"
        user.email shouldBe "new@email.com"
        user.password shouldBe "encodedPassword"
    }


    "updateUser выбрасывает ошибку, если username уже существует" {
        val userDetails = mockk<org.springframework.security.core.userdetails.UserDetails>()
        every { userDetails.username } returns "email@email.com"

        val dto = UserUpdateRequest(
            username = "existingUsername",
            email = null,
            password = null
        )

        every { repository.findByEmail("email@email.com") } returns Optional.of(user)
        every { repository.findByUsername("existingUsername") } returns Optional.of(user)

        val ex = shouldThrow<UserAlreadyExistsException> {
            userService.updateUser(userDetails, dto)
        }

        ex.message shouldBe "Пользователь с username existingUsername уже существует"
    }

    "updateUser выбрасывает ошибку, если email уже существует" {
        val userDetails = mockk<org.springframework.security.core.userdetails.UserDetails>()
        every { userDetails.username } returns "email@email.com"

        val dto = UserUpdateRequest(
            username = null,
            email = "existing@email.com",
            password = null
        )

        every { repository.findByEmail("email@email.com") } returns Optional.of(user)
        every { repository.findByEmail("existing@email.com") } returns Optional.of(user)

        val ex = shouldThrow<UserAlreadyExistsException> {
            userService.updateUser(userDetails, dto)
        }

        ex.message shouldBe "Пользователь с email existing@email.com уже существует"
    }

    //----------------deleteUser-----------------

    "deleteUser удаляет пользователя" {
        val userDetails = mockk<org.springframework.security.core.userdetails.UserDetails>()
        every { userDetails.username } returns "email@email.com"

        every { repository.findByEmail("email@email.com") } returns Optional.of(user)
        every { repository.delete(user) } returns Unit

        userService.deleteUser(userDetails)

        verify(exactly = 1) { repository.delete(user) }
    }
})