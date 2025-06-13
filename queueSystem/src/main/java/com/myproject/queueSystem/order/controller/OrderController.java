package com.myproject.queueSystem.order.controller;

import com.myproject.queueSystem.order.domain.order.Order;
import com.myproject.queueSystem.order.domain.order.OrderDTO;
import com.myproject.queueSystem.order.domain.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder() {
        return orderService.createOrder();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<OrderDTO>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeOrder(@PathVariable Long id) {
        return orderService.closeOrder(id);
    }

    @GetMapping("/total/{id}")
    public ResponseEntity<BigDecimal> calculateOrderTotal(@PathVariable Long id) {
        return orderService.calculateOrderTotal(id);
    }
}
