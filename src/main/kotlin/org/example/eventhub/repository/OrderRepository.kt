package org.example.eventhub.repository

import org.example.eventhub.entity.Order
import org.example.eventhub.enums.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface OrderRepository : JpaRepository<Order, Long> {
    @Query(
        value = "SELECT o FROM Order o WHERE o.user.id = :userId",
        countQuery = "SELECT count(*) FROM Order o WHERE o.user.id = :userId"
    )
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Order>

    fun findOrdersByStatusAndReservedUntilBefore(status: OrderStatus, now: LocalDateTime): MutableList<Order>
}
