package org.example.eventhub.service

import jakarta.transaction.Transactional
import org.example.eventhub.dto.security.RegisterRequest
import org.example.eventhub.dto.user.*
import org.example.eventhub.entity.User
import org.example.eventhub.exception.UserAlreadyExistsException
import org.example.eventhub.exception.UserNotFoundException
import org.example.eventhub.mapper.UserMapper
import org.example.eventhub.repository.UserRepository
import org.example.eventhub.specification.UserSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class UserService(
    private val repository: UserRepository,
    private val mapper: UserMapper,
    private val specification: UserSpecification,
    private val passwordEncoder: PasswordEncoder
) {

    fun createUser(dto: RegisterRequest): UserResponseLong {
        checkUsernameUnique(dto.username)
        checkEmailUnique(dto.email)

        val encodedPassword = requireNotNull(
            passwordEncoder.encode(dto.password)
        ) {
            "PasswordEncoder returned null"
        }

        val user = mapper.toEntity(dto, encodedPassword)

        return mapper.toLongDto(repository.save(user))
    }

    fun getAllUsers(
        pageable: Pageable,
        filter: UserFilter
    ): Page<UserResponseShort> =
        repository.findAll(specification.withFilter(filter), pageable)
            .map(mapper::toShortDto)

    fun getUserById(id: Long): UserResponseLong =
        mapper.toLongDto(getUserByIdAsEntity(id))

    fun getUserByUsername(username: String): UserResponseLong =
        mapper.toLongDto(getUserByUsernameAsEntity(username))

    fun updateUser(
        userDetails: UserDetails,
        dto: UserUpdateRequest
    ): UserResponseLong {
        val user = getUserByUsernameAsEntity(userDetails.username)

        dto.username?.takeIf { it != user.username }?.let {
            checkUsernameUnique(it)
            user.username = it
        }

        dto.email?.takeIf { it != user.email }?.let {
            checkEmailUnique(it)
            user.email = it
        }

        dto.password?.let {
            user.password = requireNotNull(passwordEncoder.encode(it)) {
                "PasswordEncoder returned null"
            }
        }

        return mapper.toLongDto(user)
    }

    fun deleteUser(userDetails: UserDetails) {
        val user = getUserByUsername(userDetails.username)
        repository.deleteById(user.id)
    }

    private fun checkUsernameUnique(username: String) {
        if (repository.findByUsername(username).isPresent) {
            throw UserAlreadyExistsException("Пользователь с username $username уже существует")
        }
    }

    private fun checkEmailUnique(email: String) {
        if (repository.findByEmail(email).isPresent) {
            throw UserAlreadyExistsException("Пользователь с email $email уже существует")
        }
    }

    fun getUserByIdAsEntity(id: Long): User =
        repository.findById(id)
            .orElseThrow { UserNotFoundException("Пользователь с id $id не найден") }

    fun getUserByUsernameAsEntity(username: String): User =
        repository.findByUsername(username)
            .orElseThrow { UserNotFoundException("Пользователь с username $username не найден") }
}
