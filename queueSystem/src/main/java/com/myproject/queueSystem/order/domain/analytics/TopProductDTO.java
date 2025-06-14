package com.myproject.queueSystem.order.domain.analytics;

import com.myproject.queueSystem.order.domain.product.Product;

public record TopProductDTO(Product product, Long quanty) {}

