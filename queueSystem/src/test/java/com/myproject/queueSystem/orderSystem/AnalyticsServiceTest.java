package com.myproject.queueSystem.orderSystem;

import com.myproject.queueSystem.order.domain.analytics.AnalyticsService;
import com.myproject.queueSystem.order.domain.analytics.SalesPerDateDTO;
import com.myproject.queueSystem.order.domain.analytics.TopProductDTO;
import com.myproject.queueSystem.order.domain.order.Order;
import com.myproject.queueSystem.order.domain.order.OrderRepository;
import com.myproject.queueSystem.order.domain.order.STATUS;
import com.myproject.queueSystem.order.domain.order.item.ItemRepository;
import com.myproject.queueSystem.order.domain.product.Product;
import com.myproject.queueSystem.order.domain.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AnalyticsServiceTest {

    private AnalyticsService analyticsService;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        itemRepository = mock(ItemRepository.class);

        analyticsService = new AnalyticsService();
        analyticsService.orderRepository = orderRepository;
        analyticsService.productRepository = productRepository;
        analyticsService.itemRepository = itemRepository;
    }

    @Test
    void getTopSellingProducts_shouldReturnResults() {
        Product produtoA = new Product();
        produtoA.setName("Produto A");

        Product produtoB = new Product();
        produtoB.setName("Produto B");

        List<TopProductDTO> mockResult = List.of(
                new TopProductDTO(produtoA, 10L),
                new TopProductDTO(produtoB, 5L)
        );

        when(itemRepository.findProductsMostSold(any())).thenReturn(mockResult);

        ResponseEntity<List<TopProductDTO>> response = analyticsService.getTopSellingProducts("week");

        assertEquals(2, response.getBody().size());
        assertEquals("Produto A", response.getBody().get(0).product().getName());
    }


    @Test
    void getSalesPer_shouldReturnTotalSalesGroupedByDate() {
        LocalDateTime now = LocalDateTime.now();

        Order order1 = new Order();
        order1.setStatus(STATUS.CLOSED);
        order1.setOpenedAt(now.minusDays(1));
        order1.setTotal(new BigDecimal("100"));

        Order order2 = new Order();
        order2.setStatus(STATUS.CLOSED);
        order2.setOpenedAt(now.minusDays(1));
        order2.setTotal(new BigDecimal("150"));

        Order order3 = new Order();
        order3.setStatus(STATUS.CLOSED);
        order3.setOpenedAt(now.minusDays(3));
        order3.setTotal(new BigDecimal("50"));

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2, order3));

        ResponseEntity<List<SalesPerDateDTO>> response = analyticsService.getSalesPer("week");

        List<SalesPerDateDTO> result = response.getBody();
        assertEquals(2, result.size());

        BigDecimal totalDay1 = result.stream()
                .filter(r -> r.date().equals(order1.getOpenedAt().toLocalDate()))
                .findFirst()
                .map(SalesPerDateDTO::total)
                .orElse(BigDecimal.ZERO);

        assertEquals(new BigDecimal("250"), totalDay1);
    }
}
