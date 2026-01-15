package org.example.eventhub.controller

import jakarta.validation.Valid
import org.example.eventhub.dto.order.OrderCreateRequest
import org.example.eventhub.dto.order.OrderResponseLong
import org.example.eventhub.dto.order.OrderResponseShort
import org.example.eventhub.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val service: OrderService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(
        @Valid @RequestBody dto: OrderCreateRequest,
        @AuthenticationPrincipal user: UserDetails
    ): OrderResponseLong =
        service.createOrder(dto, user)

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    fun getOrderById(@RequestParam orderId: Long): OrderResponseLong =
        service.getOrderById(orderId)

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    fun getAllOrders(@AuthenticationPrincipal user: UserDetails, pageable: Pageable): Page<OrderResponseShort> =
        service.getAllOrders(user, pageable)

    @PostMapping("/{orderId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    fun cancelOrder(@AuthenticationPrincipal user: UserDetails, @PathVariable orderId: Long) =
        service.cancelOrder(user, orderId)

    @PostMapping("/{orderId}/pay")
    @ResponseStatus(HttpStatus.OK)
    fun payOrder(@AuthenticationPrincipal user: UserDetails, @PathVariable orderId: Long) {
        service.payOrder(user, orderId)
    }
}