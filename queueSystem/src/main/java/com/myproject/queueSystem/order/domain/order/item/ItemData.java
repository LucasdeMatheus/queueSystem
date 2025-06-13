package com.myproject.queueSystem.order.domain.order.item;

import com.myproject.queueSystem.order.domain.product.Product;

import java.util.List;

public record ItemData(

        Integer quantity,
        String notes,
        Long idProduct,
        List<Product> extras
) {
}
