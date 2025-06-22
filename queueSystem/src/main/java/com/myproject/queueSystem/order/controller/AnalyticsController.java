package com.myproject.queueSystem.order.controller;


import com.myproject.queueSystem.order.domain.analytics.*;
import com.myproject.queueSystem.order.domain.order.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDTO>> getTopSellingProducts(@RequestParam(defaultValue = "all") String period) {
        return analyticsService.getTopSellingProducts(period);
    }

    @GetMapping("/sales-per-date")
    public ResponseEntity<List<SalesPerDateDTO>> getSalesPerDate(@RequestParam(defaultValue = "all") String period) {
        return analyticsService.getSalesPer(period);
    }



}

