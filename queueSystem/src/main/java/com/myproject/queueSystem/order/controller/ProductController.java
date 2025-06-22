package com.myproject.queueSystem.order.controller;

import com.myproject.queueSystem.order.domain.order.Order;
import com.myproject.queueSystem.order.domain.product.Product;
import com.myproject.queueSystem.order.domain.product.ProductService;
import com.myproject.queueSystem.order.domain.product.ProductType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product data) {
        return productService.createProduct(data);
    }

    @GetMapping
    public ResponseEntity<List<Product>> listProducts(@RequestParam(value = "type", required = false) ProductType type) {
        return productService.listProducts(type);
    }



    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product data) {
        return productService.updateProduct(id, data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

}
