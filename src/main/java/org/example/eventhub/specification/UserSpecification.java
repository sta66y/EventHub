package org.example.eventhub.specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.example.eventhub.dto.user.UserFilter;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {

    public Specification<User> withFilter(UserFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
                String lowerUsername = filter.getUsername().toLowerCase(Locale.ROOT);
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + lowerUsername + "%"));
            }

            if (filter.getRole() != null) {
                predicates.add(cb.equal(root.get("role"), filter.getRole()));
            }

            if (filter.getFromCreatedAt() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getFromCreatedAt()));
            }

            if (filter.getToCreatedAt() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getToCreatedAt()));
            }

            if (filter.getMinOrganizedEvents() != null || filter.getMaxOrganizedEvents() != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Event> eventRoot = subquery.from(Event.class);
                subquery.select(cb.count(eventRoot));
                subquery.where(cb.equal(eventRoot.get("organizer"), root));

                if (filter.getMinOrganizedEvents() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(
                            subquery, filter.getMinOrganizedEvents().longValue()));
                }
                if (filter.getMaxOrganizedEvents() != null) {
                    predicates.add(cb.lessThanOrEqualTo(
                            subquery, filter.getMaxOrganizedEvents().longValue()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
