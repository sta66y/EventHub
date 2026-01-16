package org.example.eventhub.security

import org.example.eventhub.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

private val log = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

@Service
class CustomUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        log.debug("Проверка существования пользователя с email $username в БД...")

        val user = userRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("Пользователя с email $username не найдено") }

        val authorities = user.role.permissions
            .map { SimpleGrantedAuthority(it.name) }

        return User
            .withUsername(user.email)
            .password(user.password)
            .authorities(authorities)
            .build()
    }
}
