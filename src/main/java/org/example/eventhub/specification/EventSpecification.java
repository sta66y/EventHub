package org.example.eventhub.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.eventhub.dto.event.EventFilter;
import org.example.eventhub.entity.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventSpecification {

    public Specification<Event> withFilter(EventFilter filter) {
        return (root, query, cb) -> {
           if (filter == null) {
               return cb.conjunction();
           }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.title() != null && !filter.title().isBlank()) {
                String lowerTitle = filter.title().toLowerCase();
                predicates.add( cb.like(
                        cb.lower(root.get("title")),
                        "%" + lowerTitle + "%"
                ));
            }

            if (filter.city() != null && !filter.city().isBlank()) {
                String lowerCity = filter.city().toLowerCase();
                predicates.add( cb.like(
                        cb.lower(root.get("location").get("city")),
                        "%" + lowerCity + "%"
                ));
            }

            if (filter.minCapacity() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), filter.minCapacity()));
            }
            if (filter.maxCapacity() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), filter.maxCapacity()));
            }

            if (filter.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
            }
            if (filter.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
            }

            if (filter.eventStatus() != null) {
                predicates.add(cb.equal(root.get("eventStatus"), filter.eventStatus()));
            }

            if (filter.fromDateTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("dateTime"),
                        filter.fromDateTime())
                );
            }

            if (filter.toDateTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("dateTime"),
                        filter.toDateTime())
                );
            }

            if (filter.upcoming() != null && filter.upcoming()) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("dateTime"),
                        LocalDateTime.now())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }
}
