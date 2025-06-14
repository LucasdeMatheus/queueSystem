package com.myproject.queueSystem.order.domain.analytics;

import com.myproject.queueSystem.order.domain.order.Order;
import com.myproject.queueSystem.order.domain.order.OrderRepository;
import com.myproject.queueSystem.order.domain.order.STATUS;
import com.myproject.queueSystem.order.domain.order.item.ItemRepository;
import com.myproject.queueSystem.order.domain.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemRepository itemRepository;

    public ResponseEntity<List<TopProductDTO>> getTopSellingProducts(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        switch (period.toLowerCase()) {
            case "day":
                startDate = now.truncatedTo(ChronoUnit.DAYS);
                break;
            case "week":
                startDate = now.minusDays(7).truncatedTo(ChronoUnit.DAYS);
                break;
            case "month":
                startDate = now.minusMonths(1).truncatedTo(ChronoUnit.DAYS);
                break;
            case "all":
            default:
                startDate = null; // sem filtro
        }

        List<TopProductDTO> result = itemRepository.findProductsMostSold(startDate);
        return ResponseEntity.ok(result);
    }


    public ResponseEntity<List<SalesPerDateDTO>> getSalesPer(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        switch (period.toLowerCase()) {
            case "day":
                startDate = now.truncatedTo(ChronoUnit.DAYS);
                break;
            case "week":
                startDate = now.minusDays(7).truncatedTo(ChronoUnit.DAYS);
                break;
            case "month":
                startDate = now.minusMonths(1).truncatedTo(ChronoUnit.DAYS);
                break;
            case "all":
            default:
                startDate = null; // sem filtro
        }

        List<Order> orders = orderRepository.findAll();

        Map<LocalDate, BigDecimal> salesPerDate = orders.stream()
                .filter(order -> order.getStatus() == STATUS.CLOSED &&
                        order.getOpenedAt() != null &&
                        (startDate == null || !order.getOpenedAt().isBefore(startDate)) // ⬅️ verifica se openedAt >= startDate
                )
                .collect(Collectors.groupingBy(
                        order -> order.getOpenedAt().toLocalDate(), // agrupando apenas por data
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotal, BigDecimal::add)
                ));

        List<SalesPerDateDTO> salesPerDateDTOS = salesPerDate.entrySet().stream()
                .map(entry -> new SalesPerDateDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(SalesPerDateDTO::date)) // ordenado por data
                .toList();

        return ResponseEntity.ok(salesPerDateDTOS);
    }


}
