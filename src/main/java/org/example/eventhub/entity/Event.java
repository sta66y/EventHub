package org.example.eventhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.example.eventhub.enums.EventStatus;
import org.example.eventhub.exception.NoAvailableTicketsException;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;

    @Embedded
    private Location location;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventStatus eventStatus = EventStatus.DRAFT;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Version
    private Long version;

    @Column(nullable = false)
    @Builder.Default
    private int reservedCount = 0;

    public void decrementReservedCount() {
        if (reservedCount <= 0) {
            throw new IllegalStateException("reservedCount < 0");
        }

        this.reservedCount--;
    }

    public void incrementReservedCount() {
        if (reservedCount >= capacity) {
            throw new NoAvailableTicketsException("Свободных билетов для " + this.getTitle() + " не осталось");
        }

        reservedCount++;
    }

}
