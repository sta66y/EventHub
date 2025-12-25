package org.example.eventhub.repository;

import org.example.eventhub.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(
            value = "SELECT o FROM Order o WHERE o.user.id = :userId",
            countQuery = "SELECT count(*) FROM Order o WHERE o.user.id = :userId")
    Page<Order> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
