package org.example.eventhub.entity

import jakarta.persistence.*
import org.example.eventhub.enums.TicketStatus
import java.math.BigDecimal

@Entity
@Table(name = "tickets")
class Ticket(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @Column(nullable = false)
    var price: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TicketStatus = TicketStatus.RESERVED
) {

    fun pay() {
        status = TicketStatus.PAID
    }

    fun cancel() {
        status = TicketStatus.CANCELLED
    }
}
