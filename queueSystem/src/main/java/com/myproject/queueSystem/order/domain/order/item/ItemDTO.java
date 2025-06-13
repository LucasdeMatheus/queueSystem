package com.myproject.queueSystem.order.domain.order.item;

import com.myproject.queueSystem.order.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public record ItemDTO(
        Long orderId,
        Integer quantity,
        String notes,
        Long idProduct,
        List<Product> productsExtra,
        List<Long> extras
) {
}
