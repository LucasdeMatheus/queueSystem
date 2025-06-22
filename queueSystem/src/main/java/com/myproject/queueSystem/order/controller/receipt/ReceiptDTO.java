package com.myproject.queueSystem.order.controller.receipt;

import com.myproject.queueSystem.order.domain.order.item.ItemDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReceiptDTO(
        Long idOrder,
        List<ItemDTO> itemDTOS,
        BigDecimal total,
        String code,
        LocalDateTime localDateTime
) {
}
