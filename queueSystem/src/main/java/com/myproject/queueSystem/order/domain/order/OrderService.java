package com.myproject.queueSystem.order.domain.order;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.QueueRepository;
import com.myproject.queueSystem.domain.service.QueueService;
import com.myproject.queueSystem.order.domain.order.item.Item;
import com.myproject.queueSystem.order.domain.order.item.ItemDTO;
import com.myproject.queueSystem.order.domain.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private QueueService queueService;

    @Autowired
    QueueRepository queueRepository;

    public ResponseEntity<Order> createOrder() {
        Order order = new Order();
        order.setStatus(STATUS.OPENED);
        order.setOpenedAt(LocalDateTime.now());
        orderRepository.save(order);
        return ResponseEntity.ok(order);
    }

    public ResponseEntity<List<ItemDTO>> getItemByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found or does not belong to this order"));

        List<ItemDTO> orderDTOList = order.getItems().stream()
                .map(item -> new ItemDTO(
                        item.getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getNotes(),
                        item.getProduct().getId(),
                        item.getPrice(),
                        null,
                        item.getExtraList()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOList);
    }


    public ResponseEntity<?> closeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found or does not belong to this order"));
        order.setStatus(STATUS.CLOSED);
        Queue queue = queueService.generatedQueue(null);
        order.setQueue(queue);
        queue.setOrder(order);
        orderRepository.save(order);
        queueRepository.save(queue);
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
            for (Product product : item.getExtraList()) {
                extrasPrice.add(product.getPrice());
            }


            total = total.add(basePrice).add(extrasPrice);
        }

        order.setTotal(total);
        orderRepository.save(order);

        return ResponseEntity.ok(total);
    }


    public ResponseEntity<List<OrderDTO>> getOrders() {
        List<Order> closedOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == STATUS.CLOSED)
                .collect(Collectors.toList());

        List<OrderDTO> dtoList = closedOrders.stream()
                .map(order -> {
                    List<ItemDTO> itemDTOs = order.getItems().stream()
                            .map(item -> new ItemDTO(
                                    item.getId(),
                                    item.getProduct().getName(), item.getQuantity(),
                                    item.getNotes(),
                                    item.getProduct().getId(),
                                    item.getPrice(),
                                    null,
                                    item.getExtraList()
                            ))
                            .collect(Collectors.toList());

                    return new OrderDTO(
                            order.getId(),
                            order.getQueue() == null ? null : new QueueDTO(order.getQueue().getId(), order.getQueue().getCode()),
                            order.getTotal(),
                            order.getOpenedAt(),
                            itemDTOs
                    );
                })
                .collect(Collectors.toList());


        return ResponseEntity.ok(dtoList);
    }

    public ResponseEntity<List<OrderDTO>> returnReceipt(Long id) {
    return null;}
}
