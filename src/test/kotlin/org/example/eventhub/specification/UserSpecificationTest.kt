package org.example.eventhub.specification;

import jakarta.persistence.criteria.*;
import org.example.eventhub.dto.user.UserFilter;
import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.User;
import org.example.eventhub.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSpecificationTest {

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @InjectMocks
    private UserSpecification specification;

    @Test
    @DisplayName("withFilter: должен добавлять LIKE по username (игнорируя регистр)")
    void withFilter_shouldAddUsernamePredicate_whenUsernameProvided() {
        UserFilter filter = new UserFilter("AdminUser", null, null, null, null, null);

        Path usernamePath = mock(Path.class);
        Expression<String> lowerUsernameExpr = mock(Expression.class);
        Predicate likePredicate = mock(Predicate.class);

        when(root.get("username")).thenReturn(usernamePath);
        when(cb.lower(usernamePath)).thenReturn(lowerUsernameExpr);
        when(cb.like(lowerUsernameExpr, "%adminuser%")).thenReturn(likePredicate);

        Specification<User> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).lower(usernamePath);
        verify(cb).like(lowerUsernameExpr, "%adminuser%");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять equal по role")
    void withFilter_shouldAddRolePredicate_whenRoleProvided() {
        UserFilter filter = new UserFilter(null, Role.ORGANIZER, null, null, null, null);

        Path rolePath = mock(Path.class);
        Predicate equalPredicate = mock(Predicate.class);

        when(root.get("role")).thenReturn(rolePath);
        when(cb.equal(rolePath, Role.ORGANIZER)).thenReturn(equalPredicate);

        Specification<User> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).equal(rolePath, Role.ORGANIZER);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять greaterThanOrEqualTo по fromCreatedAt")
    void withFilter_shouldAddFromCreatedAtPredicate_whenFromProvided() {
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        UserFilter filter = new UserFilter(null, null, from, null, null, null);

        Path createdAtPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("createdAt")).thenReturn(createdAtPath);
        when(cb.greaterThanOrEqualTo(createdAtPath, from)).thenReturn(predicate);

        Specification<User> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(createdAtPath, from);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять lessThanOrEqualTo по toCreatedAt")
    void withFilter_shouldAddToCreatedAtPredicate_whenToProvided() {
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 23, 59);
        UserFilter filter = new UserFilter(null, null, null, to, null, null);

        Path createdAtPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("createdAt")).thenReturn(createdAtPath);
        when(cb.lessThanOrEqualTo(createdAtPath, to)).thenReturn(predicate);

        Specification<User> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(createdAtPath, to);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять субквери с minOrganizedEvents")
    void withFilter_shouldAddMinOrganizedEventsSubquery_whenMinProvided() {
        Integer minEvents = 5;
        UserFilter filter = new UserFilter(null, null, null, null, minEvents, null);

        Subquery<Long> subquery = mock(Subquery.class);
        Root<Event> eventRoot = mock(Root.class);
        Expression<Long> countExpr = mock(Expression.class);
        Predicate organizerPredicate = mock(Predicate.class);
        Predicate gePredicate = mock(Predicate.class);

        when(query.subquery(Long.class)).thenReturn(subquery);
        when(subquery.from(Event.class)).thenReturn(eventRoot);
        when(cb.count(eventRoot)).thenReturn(countExpr);
        when(eventRoot.get("organizer")).thenReturn(mock(Path.class));
        when(cb.equal(any(), eq(root))).thenReturn(organizerPredicate);
        when(cb.greaterThanOrEqualTo(eq(subquery), eq(minEvents.longValue()))).thenReturn(gePredicate);

        Specification<User> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(subquery).select(countExpr);
        verify(subquery).where(organizerPredicate);
        verify(cb).greaterThanOrEqualTo(subquery, minEvents.longValue());
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять субквери с maxOrganizedEvents")
    void withFilter_shouldAddMaxOrganizedEventsSubquery_whenMaxProvided() {
        Integer maxEvents = 20;
        UserFilter filter = new UserFilter(null, null, null, null, null, maxEvents);

        Subquery<Long> subquery = mock(Subquery.class);
        Root<Event> eventRoot = mock(Root.class);
        Expression<Long> countExpr = mock(Expression.class);
        Predicate organizerPredicate = mock(Predicate.class);
        Predicate lePredicate = mock(Predicate.class);

        when(query.subquery(Long.class)).thenReturn(subquery);
        when(subquery.from(Event.class)).thenReturn(eventRoot);
        when(cb.count(eventRoot)).thenReturn(countExpr);
        when(cb.equal(any(), eq(root))).thenReturn(organizerPredicate);
        when(cb.lessThanOrEqualTo(eq(subquery), eq(maxEvents.longValue()))).thenReturn(lePredicate);

        Specification<User> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(subquery).select(countExpr);
        verify(subquery).where(organizerPredicate);
        verify(cb).lessThanOrEqualTo(subquery, maxEvents.longValue());
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять субквери с min и max OrganizedEvents одновременно")
    void withFilter_shouldAddBothMinAndMaxOrganizedEventsSubquery_whenBothProvided() {
        UserFilter filter = new UserFilter(null, null, null, null, 3, 15);

        Subquery<Long> subquery = mock(Subquery.class);
        Root<Event> eventRoot = mock(Root.class);
        Expression<Long> countExpr = mock(Expression.class);
        Predicate organizerPredicate = mock(Predicate.class);

        when(query.subquery(Long.class)).thenReturn(subquery);
        when(subquery.from(Event.class)).thenReturn(eventRoot);
        when(cb.count(eventRoot)).thenReturn(countExpr);
        when(cb.equal(any(), eq(root))).thenReturn(organizerPredicate);

        Specification<User> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(subquery).select(countExpr);
        verify(subquery).where(organizerPredicate);
        verify(cb).greaterThanOrEqualTo(subquery, 3L);
        verify(cb).lessThanOrEqualTo(subquery, 15L);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен возвращать conjunction при filter = null")
    void withFilter_shouldReturnConjunction_whenFilterIsNull() {
        Specification<User> spec = specification.withFilter(null);
        spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }
}