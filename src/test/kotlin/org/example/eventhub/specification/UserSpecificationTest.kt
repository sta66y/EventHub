package org.example.eventhub.specification

import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import jakarta.persistence.criteria.*
import org.example.eventhub.dto.user.UserFilter
import org.example.eventhub.entity.Event
import org.example.eventhub.entity.User
import org.example.eventhub.enums.Role
import java.time.LocalDateTime

class UserSpecificationTest : StringSpec({

    val root = mockk<Root<User>>()
    val query = mockk<CriteriaQuery<*>>()
    val cb = mockk<CriteriaBuilder>()

    val specification = UserSpecification()

    beforeTest {
        clearMocks(root, query, cb)
        every { cb.conjunction() } returns mockk()
        every { cb.and(*anyVararg()) } returns mockk()
    }

    "withFilter добавляет LIKE по username" {
        val filter = UserFilter("AdminUser", null, null, null, null, null)

        val usernamePath = mockk<Path<String>>()
        val lowerExpr = mockk<Expression<String>>()
        val predicate = mockk<Predicate>()

        every { root.get<String>("username") } returns usernamePath
        every { cb.lower(usernamePath) } returns lowerExpr
        every { cb.like(lowerExpr, "%adminuser%") } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.lower(usernamePath)
            cb.like(lowerExpr, "%adminuser%")
            cb.and(any())
        }
    }

    "withFilter добавляет equal по role" {
        val filter = UserFilter(null, Role.ORGANIZER, null, null, null, null)

        val rolePath = mockk<Path<Role>>()
        val predicate = mockk<Predicate>()

        every { root.get<Role>("role") } returns rolePath
        every { cb.equal(rolePath, Role.ORGANIZER) } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.equal(rolePath, Role.ORGANIZER)
            cb.and(any())
        }
    }

    "withFilter добавляет fromCreatedAt" {
        val from = LocalDateTime.of(2024, 1, 1, 0, 0)
        val filter = UserFilter(null, null, from, null, null, null)

        val createdAtPath = mockk<Path<LocalDateTime>>()
        val predicate = mockk<Predicate>()

        every { root.get<LocalDateTime>("createdAt") } returns createdAtPath
        every { cb.greaterThanOrEqualTo(createdAtPath, from) } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.greaterThanOrEqualTo(createdAtPath, from)
            cb.and(any())
        }
    }

    "withFilter добавляет toCreatedAt" {
        val to = LocalDateTime.of(2025, 12, 31, 23, 59)
        val filter = UserFilter(null, null, null, to, null, null)

        val createdAtPath = mockk<Path<LocalDateTime>>()
        val predicate = mockk<Predicate>()

        every { root.get<LocalDateTime>("createdAt") } returns createdAtPath
        every { cb.lessThanOrEqualTo(createdAtPath, to) } returns predicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            cb.lessThanOrEqualTo(createdAtPath, to)
            cb.and(any())
        }
    }

    "withFilter добавляет subquery с minOrganizedEvents" {
        val filter = UserFilter(null, null, null, null, 5, null)

        val subquery = mockk<Subquery<Long>>()
        val eventRoot = mockk<Root<Event>>()
        val countExpr = mockk<Expression<Long>>()
        val organizerPath = mockk<Path<User>>()
        val organizerPredicate = mockk<Predicate>()
        val gePredicate = mockk<Predicate>()

        every { query.subquery(Long::class.java) } returns subquery
        every { subquery.from(Event::class.java) } returns eventRoot
        every { cb.count(eventRoot) } returns countExpr

        every { subquery.select(countExpr) } returns subquery
        every { subquery.where(any<Predicate>()) } returns subquery

        every { eventRoot.get<User>("organizer") } returns organizerPath
        every { cb.equal(organizerPath, root) } returns organizerPredicate
        every { cb.greaterThanOrEqualTo(subquery, 5L) } returns gePredicate

        specification.withFilter(filter).toPredicate(root, query, cb)

        verify {
            subquery.select(countExpr)
            subquery.where(organizerPredicate)
            cb.greaterThanOrEqualTo(subquery, 5L)
            cb.and(any())
        }
    }

    "withFilter возвращает conjunction если filter = null" {
        specification.withFilter(null).toPredicate(root, query, cb)

        verify { cb.conjunction() }
    }
})
