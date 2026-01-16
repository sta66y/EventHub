package org.example.eventhub.specification;

import jakarta.persistence.criteria.*;
import org.example.eventhub.dto.event.EventFilter;
import org.example.eventhub.entity.Event;
import org.example.eventhub.enums.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSpecificationTest {

    @Mock
    private Root<Event> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @InjectMocks
    private EventSpecification specification;

    @Test
    @DisplayName("withFilter: должен добавлять LIKE по title (игнорируя регистр)")
    void withFilter_shouldAddTitlePredicate_whenTitleProvided() {
        EventFilter filter = new EventFilter("Rock Festival", null, null, null,
                null, null, null, null, null, null);

        Path titlePath = mock(Path.class);
        Expression<String> lowerTitleExpr = mock(Expression.class);
        Predicate likePredicate = mock(Predicate.class);

        when(root.get("title")).thenReturn(titlePath);
        when(cb.lower(titlePath)).thenReturn(lowerTitleExpr);
        when(cb.like(lowerTitleExpr, "%rock festival%")).thenReturn(likePredicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).lower(titlePath);
        verify(cb).like(lowerTitleExpr, "%rock festival%");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять LIKE по city (игнорируя регистр)")
    void withFilter_shouldAddCityPredicate_whenCityProvided() {
        EventFilter filter = new EventFilter(null, "Kansk", null, null,
                null, null, null, null, null, null);

        Path locationPath = mock(Path.class);
        Path cityPath = mock(Path.class);
        Expression<String> lowerCityExpr = mock(Expression.class);
        Predicate likePredicate = mock(Predicate.class);

        when(root.get("location")).thenReturn(locationPath);
        when(locationPath.get("city")).thenReturn(cityPath);
        when(cb.lower(cityPath)).thenReturn(lowerCityExpr);
        when(cb.like(lowerCityExpr, "%kansk%")).thenReturn(likePredicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).lower(cityPath);
        verify(cb).like(lowerCityExpr, "%kansk%");
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять greaterThanOrEqualTo по minCapacity")
    void withFilter_shouldAddMinCapacityPredicate_whenMinCapacityProvided() {
        Integer minCapacity = 100;
        EventFilter filter = new EventFilter(null, null, minCapacity, null,
                null, null, null, null, null, null);

        Path capacityPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("capacity")).thenReturn(capacityPath);
        when(cb.greaterThanOrEqualTo(capacityPath, minCapacity)).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(capacityPath, minCapacity);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять lessThanOrEqualTo по maxCapacity")
    void withFilter_shouldAddMaxCapacityPredicate_whenMaxCapacityProvided() {
        Integer maxCapacity = 500;
        EventFilter filter = new EventFilter(null, null, null, maxCapacity,
                null, null, null, null, null, null);

        Path capacityPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("capacity")).thenReturn(capacityPath);
        when(cb.lessThanOrEqualTo(capacityPath, maxCapacity)).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(capacityPath, maxCapacity);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять greaterThanOrEqualTo по minPrice")
    void withFilter_shouldAddMinPricePredicate_whenMinPriceProvided() {
        BigDecimal minPrice = BigDecimal.ZERO;
        EventFilter filter = new EventFilter(null, null, null, null,
                minPrice, null, null, null, null, null);

        Path pricePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("price")).thenReturn(pricePath);
        when(cb.greaterThanOrEqualTo(pricePath, minPrice)).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(pricePath, minPrice);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять lessThanOrEqualTo по maxPrice")
    void withFilter_shouldAddMaxPricePredicate_whenMaxPriceProvided() {
        BigDecimal maxPrice = BigDecimal.TEN;
        EventFilter filter = new EventFilter(null, null, null, null,
                null, maxPrice, null, null, null, null);

        Path pricePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("price")).thenReturn(pricePath);
        when(cb.lessThanOrEqualTo(pricePath, maxPrice)).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(pricePath, maxPrice);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять equal по eventStatus")
    void withFilter_shouldAddEventStatusPredicate_whenStatusProvided() {
        EventFilter filter = new EventFilter(null, null, null, null,
                null, null, EventStatus.DRAFT, null, null, null);

        Path statusPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("eventStatus")).thenReturn(statusPath);
        when(cb.equal(statusPath, filter.getEventStatus())).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).equal(statusPath, filter.getEventStatus());
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять greaterThanOrEqualTo по fromDateTime")
    void withFilter_shouldAddFromDateTimePredicate_whenFromDateTimeProvided() {
        LocalDateTime from = LocalDateTime.of(2025, 12, 1, 0, 0);
        EventFilter filter = new EventFilter(null, null, null, null,
                null, null, null, from, null, null);

        Path dateTimePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("dateTime")).thenReturn(dateTimePath);
        when(cb.greaterThanOrEqualTo(dateTimePath, from)).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(dateTimePath, from);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять lessThanOrEqualTo по toDateTime")
    void withFilter_shouldAddToDateTimePredicate_whenToDateTimeProvided() {
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 23, 59);
        EventFilter filter = new EventFilter(null, null, null, null,
                null, null, null, null, to, null);

        Path dateTimePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("dateTime")).thenReturn(dateTimePath);
        when(cb.lessThanOrEqualTo(dateTimePath, to)).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(dateTimePath, to);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен добавлять upcoming фильтр (dateTime >= now)")
    void withFilter_shouldAddUpcomingPredicate_whenUpcomingTrue() {
        EventFilter filter = new EventFilter(null, null, null, null,
                null, null, null, null, null, true);

        Path dateTimePath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("dateTime")).thenReturn(dateTimePath);
        when(cb.greaterThanOrEqualTo(eq(dateTimePath), any(LocalDateTime.class))).thenReturn(predicate);

        Specification<Event> spec = specification.withFilter(filter);
        spec.toPredicate(root, query, cb);

        verify(cb).greaterThanOrEqualTo(eq(dateTimePath), any(LocalDateTime.class));
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("withFilter: должен возвращать conjunction при filter = null")
    void withFilter_shouldReturnConjunction_whenFilterIsNull() {
        Specification<Event> spec = specification.withFilter(null);
        Predicate result = spec.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }
}