package org.example.eventhub.specification

import jakarta.persistence.criteria.Predicate
import org.example.eventhub.dto.event.EventFilter
import org.example.eventhub.entity.Event
import org.example.eventhub.entity.Location
import org.example.eventhub.enums.EventStatus
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EventSpecification {

    fun withFilter(filter: EventFilter?): Specification<Event> =
        Specification { root, _, cb ->

            if (filter == null) {
                return@Specification cb.conjunction()
            }

            val predicates = mutableListOf<Predicate>()

            filter.title
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    predicates += cb.like(
                        cb.lower(root.get("title")),
                        "%${it.lowercase()}%"
                    )
                }

            filter.city
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    predicates += cb.like(
                        cb.lower(root.get<Location>("location").get("city")),
                        "%${it.lowercase()}%"
                    )
                }

            filter.minCapacity?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("capacity"), it)
            }

            filter.maxCapacity?.let {
                predicates += cb.lessThanOrEqualTo(root.get("capacity"), it)
            }

            filter.minPrice?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("price"), it)
            }

            filter.maxPrice?.let {
                predicates += cb.lessThanOrEqualTo(root.get("price"), it)
            }

            filter.eventStatus?.let {
                predicates += cb.equal(root.get<EventStatus>("eventStatus"), it)
            }

            filter.fromDateTime?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("dateTime"), it)
            }

            filter.toDateTime?.let {
                predicates += cb.lessThanOrEqualTo(root.get("dateTime"), it)
            }

            if (filter.upcoming == true) {
                predicates += cb.greaterThanOrEqualTo(
                    root.get("dateTime"),
                    LocalDateTime.now()
                )
            }

            cb.and(*predicates.toTypedArray())
        }
}
