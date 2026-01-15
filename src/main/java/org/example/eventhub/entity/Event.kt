package org.example.eventhub.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import org.example.eventhub.enums.EventStatus
import org.example.eventhub.exception.NoAvailableTicketsException

@Entity
class Event(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    var description: String? = null,

    @Column(nullable = false, name = "date_time")
    var dateTime: LocalDateTime,

    @Embedded
    var location: Location? = null,

    @Column(nullable = false)
    var capacity: Int,

    @Column(nullable = false)
    var price: BigDecimal = BigDecimal.ZERO,

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    var organizer: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var eventStatus: EventStatus = EventStatus.DRAFT,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tickets: MutableList<Ticket> = mutableListOf(),

    @Version
    var version: Long? = null,

    @Column(nullable = false)
    var reservedCount: Int = 0
) {

    fun decrementReservedCount() {
        if (reservedCount <= 0) {
            throw IllegalStateException("reservedCount < 0")
        }
        reservedCount--
    }

    fun incrementReservedCount() {
        if (reservedCount >= capacity) {
            throw NoAvailableTicketsException(
                "Свободных билетов для $title не осталось"
            )
        }
        reservedCount++
    }
}
