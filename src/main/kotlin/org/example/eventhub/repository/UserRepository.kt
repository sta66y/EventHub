package org.example.eventhub.repository

import org.example.eventhub.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    fun findByEmail(email: String): Optional<User>

    fun findByUsername(username: String): Optional<User>
}
