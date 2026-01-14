package org.example.eventhub.security

import org.example.eventhub.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("Пользователя с email $username не найдено") }

        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password(user.password)
            .roles(user.role.name)
            .build()
    }
}
