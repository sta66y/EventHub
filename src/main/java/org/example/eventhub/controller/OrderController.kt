package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.order.OrderCreateRequest
import org.example.eventhub.dto.order.OrderResponseLong
import org.example.eventhub.dto.order.OrderResponseShort
import org.example.eventhub.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController (
    private val service: OrderService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createOrder(
        @Valid @RequestBody dto: OrderCreateRequest,
        @RequestParam userId: Long
    ): OrderResponseLong {
        return service.createOrder(dto, userId)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getOrderById(
        @PathVariable id: Long
    ): OrderResponseLong {
        return service.getOrderById(id)
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    fun getAllOrders(
        @RequestParam userId: Long,
        pageable: Pageable
    ): Page<OrderResponseShort> {
        return service.getAllOrders(userId, pageable)
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    fun cancelOrder(
        @PathVariable id: Long
    ) {
        service.cancelOrder(id)
    }

    @PostMapping("/{id}/pay")
    @ResponseStatus(HttpStatus.OK)
    fun payOrder(
        @PathVariable id: Long
    ) {
        service.payOrder(id)
    }
}