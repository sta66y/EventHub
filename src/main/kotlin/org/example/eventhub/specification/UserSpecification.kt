package org.example.eventhub.specification

import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.persistence.criteria.Subquery
import org.example.eventhub.dto.user.UserFilter
import org.example.eventhub.entity.Event
import org.example.eventhub.entity.User
import org.example.eventhub.enums.Role
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class UserSpecification {

    fun withFilter(filter: UserFilter?): Specification<User> =
        Specification { root, query, cb ->

            if (filter == null) {
                return@Specification cb.conjunction()
            }

            val predicates = mutableListOf<Predicate>()

            filter.username
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    predicates += cb.like(
                        cb.lower(root.get<String>("username")),
                        "%${it.lowercase(Locale.ROOT)}%"
                    )
                }

            filter.role?.let {
                predicates += cb.equal(
                    root.get<Role>("role"),
                    it
                )
            }

            filter.fromCreatedAt?.let {
                predicates += cb.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    it
                )
            }

            filter.toCreatedAt?.let {
                predicates += cb.lessThanOrEqualTo(
                    root.get("createdAt"),
                    it
                )
            }

            if (filter.minOrganizedEvents != null || filter.maxOrganizedEvents != null) {

                val subquery: Subquery<Long> = query.subquery(Long::class.java)
                val eventRoot: Root<Event> = subquery.from(Event::class.java)

                subquery.select(cb.count(eventRoot))
                subquery.where(
                    cb.equal(
                        eventRoot.get<User>("organizer"),
                        root
                    )
                )

                filter.minOrganizedEvents?.let {
                    predicates += cb.greaterThanOrEqualTo(
                        subquery,
                        it.toLong()
                    )
                }

                filter.maxOrganizedEvents?.let {
                    predicates += cb.lessThanOrEqualTo(
                        subquery,
                        it.toLong()
                    )
                }
            }

            cb.and(*predicates.toTypedArray())
        }
}
