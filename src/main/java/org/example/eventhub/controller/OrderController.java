package org.example.eventhub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eventhub.dto.order.OrderCreateRequest;
import org.example.eventhub.dto.order.OrderResponseLong;
import org.example.eventhub.dto.order.OrderResponseShort;
import org.example.eventhub.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseLong createOrder(@Valid @RequestBody OrderCreateRequest dto, @RequestParam Long userId) {
        return service.createOrder(dto, userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseLong getOrderById(@PathVariable Long id) {
        return service.getOrderById(id);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponseShort> getAllOrders(@RequestParam Long userId, Pageable pageable) {
        return service.getAllOrders(userId, pageable);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseLong cancelOrder(
            @PathVariable Long id) { // TODO короч надо придумать как точечно возвращать билеты
        return service.cancelOrder(id);
    }
}
