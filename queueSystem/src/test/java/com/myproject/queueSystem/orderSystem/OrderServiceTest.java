package com.myproject.queueSystem.orderSystem;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.QueueRepository;
import com.myproject.queueSystem.domain.service.QueueService;
import com.myproject.queueSystem.order.domain.order.*;
import com.myproject.queueSystem.order.domain.order.item.Item;
import com.myproject.queueSystem.order.domain.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private QueueRepository queueRepository;
    private QueueService queueService;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        queueRepository = mock(QueueRepository.class);
        queueService = mock(QueueService.class);

        orderService = new OrderService();
        orderService.orderRepository = orderRepository;
        orderService.queueRepository = queueRepository;
        orderService.queueService = queueService;
    }

    @Test
    void createOrder_shouldReturnOrderWithStatusOpened() {
        Order order = new Order();
        order.setStatus(STATUS.OPENED);
        order.setOpenedAt(LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        ResponseEntity<Order> response = orderService.createOrder();

        assertEquals(STATUS.OPENED, response.getBody().getStatus());
        assertNotNull(response.getBody().getOpenedAt());
    }

    @Test
    void closeOrder_shouldSetStatusAndQueue() {
        Order order = new Order();
        order.setId(1L);
        order.setItems(new ArrayList<>());
        order.setStatus(STATUS.OPENED);

        Queue queue = new Queue();
        queue.setCode("0001");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(queueService.generatedQueue(null)).thenReturn(queue);
        when(orderRepository.save(any())).thenReturn(order);
        when(queueRepository.save(any())).thenReturn(queue);

        ResponseEntity<?> response = orderService.closeOrder(1L);

        assertTrue((Boolean) response.getBody());
        assertEquals(STATUS.CLOSED, order.getStatus());
        assertEquals(queue, order.getQueue());
    }

    @Test
    void calculateOrderTotal_shouldSumBaseAndExtras() {
        Product extra1 = new Product();
        extra1.setPrice(new BigDecimal("2.00"));

        Product extra2 = new Product();
        extra2.setPrice(new BigDecimal("3.00"));

        Item item = new Item();
        item.setPrice(new BigDecimal("10.00"));
        item.setExtraList(List.of(extra1, extra2));

        Order order = new Order();
        order.setId(1L);
        order.setItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<BigDecimal> response = orderService.calculateOrderTotal(1L);

        assertEquals(new BigDecimal("15.00"), response.getBody());
        verify(orderRepository).save(order);
    }

    @Test
    void getOrders_shouldReturnOnlyClosedOrders() {
        Order closedOrder = new Order();
        closedOrder.setStatus(STATUS.CLOSED);
        closedOrder.setId(1L);
        closedOrder.setOpenedAt(LocalDateTime.now());
        closedOrder.setItems(new ArrayList<>());

        when(orderRepository.findAll()).thenReturn(List.of(closedOrder));

        ResponseEntity<List<OrderDTO>> response = orderService.getOrders();

        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).id());
    }
}
