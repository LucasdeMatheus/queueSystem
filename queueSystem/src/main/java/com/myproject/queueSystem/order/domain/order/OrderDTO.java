package com.myproject.queueSystem.order.domain.order;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.order.domain.order.item.ItemDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        QueueDTO queueDTO,
        BigDecimal total,
        LocalDateTime openedAt,
        List<ItemDTO> items
) {}

