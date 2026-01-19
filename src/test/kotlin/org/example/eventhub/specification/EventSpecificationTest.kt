package org.example.eventhub.specification

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import jakarta.persistence.criteria.*
import org.example.eventhub.dto.event.EventFilter
import org.example.eventhub.entity.Event
import org.example.eventhub.enums.EventStatus
import java.math.BigDecimal
import java.time.LocalDateTime

class EventSpecificationTest : StringSpec({

    val root = mockk<Root<Event>>()
    val query = mockk<CriteriaQuery<*>>()
    val cb = mockk<CriteriaBuilder>()

    val specification = EventSpecification()

    beforeTest {
        clearMocks(root, query, cb)
        every { cb.conjunction() } returns mockk()
        every { cb.and(*anyVararg()) } returns mockk()
    }

    "withFilter добавляет LIKE по title (без учёта регистра)" {
        val filter = EventFilter(
            title = "Rock Festival",
            city = null,
            minCapacity = null,
            maxCapacity = null,
            minPrice = null,
            maxPrice = null,
            eventStatus = null,
            fromDateTime = null,
            toDateTime = null,
            upcoming = null
        )

        val titlePath = mockk<Path<String>>()
        val lowerExpr = mockk<Expression<String>>()
        val predicate = mockk<Predicate>()

        every { root.get<String>("title") } returns titlePath
        every { cb.lower(titlePath) } returns lowerExpr
        every { cb.like(lowerExpr, "%rock festival%") } returns predicate

        val spec = specification.withFilter(filter)
        val result = spec.toPredicate(root, query, cb)

        result shouldNotBe null

        verify {
            cb.lower(titlePath)
            cb.like(lowerExpr, "%rock festival%")
            cb.and(any())
        }
    }

    "withFilter добавляет LIKE по city (без учёта регистра)" {
        val filter = EventFilter(
            title = null,
            city = "Kansk",
            minCapacity = null,
            maxCapacity = null,
            minPrice = null,
            maxPrice = null,
            eventStatus = null,
            fromDateTime = null,
            toDateTime = null,
            upcoming = null
        )

        val locationPath = mockk<Path<Any>>()
        val cityPath = mockk<Path<String>>()
        val lowerExpr = mockk<Expression<String>>()
        val predicate = mockk<Predicate>()

        every { root.get<Any>("location") } returns locationPath
        every { locationPath.get<String>("city") } returns cityPath
        every { cb.lower(cityPath) } returns lowerExpr
        every { cb.like(lowerExpr, "%kansk%") } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.lower(cityPath)
            cb.like(lowerExpr, "%kansk%")
            cb.and(any())
        }
    }

    "withFilter добавляет minCapacity фильтр" {
        val filter = EventFilter(
            title = null,
            city = null,
            minCapacity = 100,
            maxCapacity = null,
            minPrice = null,
            maxPrice = null,
            eventStatus = null,
            fromDateTime = null,
            toDateTime = null,
            upcoming = null
        )

        val capacityPath = mockk<Path<Int>>()
        val predicate = mockk<Predicate>()

        every { root.get<Int>("capacity") } returns capacityPath
        every { cb.greaterThanOrEqualTo(capacityPath, 100) } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.greaterThanOrEqualTo(capacityPath, 100)
            cb.and(any())
        }
    }

    "withFilter добавляет maxPrice фильтр" {
        val filter = EventFilter(
            title = null,
            city = null,
            minCapacity = null,
            maxCapacity = null,
            minPrice = null,
            maxPrice = BigDecimal.TEN,
            eventStatus = null,
            fromDateTime = null,
            toDateTime = null,
            upcoming = null
        )

        val pricePath = mockk<Path<BigDecimal>>()
        val predicate = mockk<Predicate>()

        every { root.get<BigDecimal>("price") } returns pricePath
        every { cb.lessThanOrEqualTo(pricePath, BigDecimal.TEN) } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.lessThanOrEqualTo(pricePath, BigDecimal.TEN)
            cb.and(any())
        }
    }

    "withFilter добавляет eventStatus фильтр" {
        val filter = EventFilter(
            title = null,
            city = null,
            minCapacity = null,
            maxCapacity = null,
            minPrice = null,
            maxPrice = null,
            eventStatus = EventStatus.DRAFT,
            fromDateTime = null,
            toDateTime = null,
            upcoming = null
        )

        val statusPath = mockk<Path<EventStatus>>()
        val predicate = mockk<Predicate>()

        every { root.get<EventStatus>("eventStatus") } returns statusPath
        every { cb.equal(statusPath, EventStatus.DRAFT) } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.equal(statusPath, EventStatus.DRAFT)
            cb.and(any())
        }
    }

    "withFilter добавляет upcoming фильтр (dateTime >= now)" {
        val filter = EventFilter(
            title = null,
            city = null,
            minCapacity = null,
            maxCapacity = null,
            minPrice = null,
            maxPrice = null,
            eventStatus = null,
            fromDateTime = null,
            toDateTime = null,
            upcoming = true
        )

        val dateTimePath = mockk<Path<LocalDateTime>>()
        val predicate = mockk<Predicate>()

        every { root.get<LocalDateTime>("dateTime") } returns dateTimePath
        every {
            cb.greaterThanOrEqualTo(eq(dateTimePath), any<LocalDateTime>())
        } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.greaterThanOrEqualTo(eq(dateTimePath), any<LocalDateTime>())
            cb.and(any())
        }
    }

    "withFilter возвращает conjunction если filter = null" {
        specification.withFilter(null).toPredicate(root, query, cb)

        verify { cb.conjunction() }
    }
})
