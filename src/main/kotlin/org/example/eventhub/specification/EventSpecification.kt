package org.example.eventhub.specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.eventhub.dto.event.EventFilter;
import org.example.eventhub.entity.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EventSpecification {

    public Specification<Event> withFilter(EventFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                String lowerTitle = filter.getTitle().toLowerCase();
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + lowerTitle + "%"));
            }

            if (filter.getCity() != null && !filter.getCity().isBlank()) {
                String lowerCity = filter.getCity().toLowerCase();
                predicates.add(cb.like(cb.lower(root.get("location").get("city")), "%" + lowerCity + "%"));
            }

            if (filter.getMinCapacity() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), filter.getMinCapacity()));
            }
            if (filter.getMaxCapacity() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), filter.getMaxCapacity()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            if (filter.getEventStatus() != null) {
                predicates.add(cb.equal(root.get("eventStatus"), filter.getEventStatus()));
            }

            if (filter.getFromDateTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), filter.getFromDateTime()));
            }

            if (filter.getToDateTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), filter.getToDateTime()));
            }

            if (filter.getUpcoming() != null && filter.getUpcoming()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), LocalDateTime.now()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
