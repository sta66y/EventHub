package org.example.eventhub.repository;

import org.example.eventhub.entity.Event;
import org.example.eventhub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
