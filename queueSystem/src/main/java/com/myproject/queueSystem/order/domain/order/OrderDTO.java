package com.myproject.queueSystem.order.domain.order;

import com.myproject.queueSystem.order.domain.order.item.ItemDTO;
import com.myproject.queueSystem.order.domain.product.Product;

import java.util.List;

public record OrderDTO(
        Long id,
        String nameProduct,
        Integer quantity,
        List<Product> productsExtra
) {}

