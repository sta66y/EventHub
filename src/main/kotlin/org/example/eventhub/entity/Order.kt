package org.example.eventhub.entity

import jakarta.persistence.*
import org.example.eventhub.enums.OrderStatus
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var tickets: MutableList<Ticket> = mutableListOf(),

    @Column(nullable = false)
    var totalPrice: BigDecimal,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(nullable = false)
    var reservedUntil: LocalDateTime
)
