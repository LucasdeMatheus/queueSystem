package com.myproject.queueSystem.order.domain.order;

import com.myproject.queueSystem.domain.service.QueueService;
import com.myproject.queueSystem.order.domain.order.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QueueService queueService;

    public ResponseEntity<Order> createOrder() {
        Order order = new Order();
        order.setStatus(STATUS.OPENED);
        order.setOpenedAt(LocalDateTime.now());
        orderRepository.save(order);
        return ResponseEntity.ok(order);
    }

    public ResponseEntity<List<OrderDTO>> getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found or does not belong to this order"));

        List<OrderDTO> orderDTOList = order.getItems().stream()
                .map(item -> new OrderDTO(
                        item.getId(),
                        item.getProduct().getName(),   // Corrigi o nome do campo (nameProduct)
                        item.getQuantity(),
                        item.getExtraList()            // assumo que é List<Product> mesmo
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOList);
    }


    public ResponseEntity<?> closeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found or does not belong to this order"));
        order.setStatus(STATUS.CLOSED);
        orderRepository.save(order);
        queueService.generatedQueue(null);
        calculateOrderTotal(id);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<BigDecimal> calculateOrderTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found or does not belong to this order"));

        BigDecimal total = BigDecimal.ZERO;

        for (Item item : order.getItems()) {
            BigDecimal basePrice = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            BigDecimal extrasPrice = BigDecimal.ZERO;


            total = total.add(basePrice).add(extrasPrice);
        }

        order.setTotal(total);
        orderRepository.save(order);

        return ResponseEntity.ok(total);
    }


}
