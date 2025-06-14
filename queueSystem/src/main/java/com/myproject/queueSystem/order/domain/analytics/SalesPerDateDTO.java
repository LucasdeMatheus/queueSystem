package com.myproject.queueSystem.order.domain.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesPerDateDTO(
        LocalDate date,
        BigDecimal total
) {
}
