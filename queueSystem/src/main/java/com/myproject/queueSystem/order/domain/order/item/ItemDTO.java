package com.myproject.queueSystem.order.domain.order.item;

import com.myproject.queueSystem.order.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public record ItemDTO(
        Long id,
        String name,
        Integer quantity,
        String notes,
        Long idProduct,
        BigDecimal price,
        List<Long> extras, // IDs dos extras enviados do front
        List<Product> productsExtra // Extras completos retornados do back
) {}
