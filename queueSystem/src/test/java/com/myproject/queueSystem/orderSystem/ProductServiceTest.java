package com.myproject.queueSystem.orderSystem;

import com.myproject.queueSystem.order.domain.order.item.ItemRepository;
import com.myproject.queueSystem.order.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        itemRepository = mock(ItemRepository.class);
        productService = new ProductService();

        productService.productRepository = productRepository;
        productService.itemRepository = itemRepository;
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza");
        product.setPrice(new BigDecimal("25.00"));
        product.setType(ProductType.FOOD);

        when(productRepository.getReferenceById(1L)).thenReturn(product);

        ResponseEntity<Product> response = productService.createProduct(product);

        verify(productRepository).save(product);
        assertEquals("Pizza", response.getBody().getName());
    }

    @Test
    void listProducts_shouldReturnAllProductsWhenTypeIsNull() {
        Product p1 = new Product();
        p1.setName("Pizza");
        Product p2 = new Product();
        p2.setName("Suco");

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        ResponseEntity<List<Product>> response = productService.listProducts(null);

        assertEquals(2, response.getBody().size());
    }

    @Test
    void listProducts_shouldReturnFilteredProductsByType() {
        Product p1 = new Product();
        p1.setName("Pizza");
        when(productRepository.findAllByType(ProductType.FOOD)).thenReturn(List.of(p1));

        ResponseEntity<List<Product>> response = productService.listProducts(ProductType.FOOD);

        assertEquals(1, response.getBody().size());
        assertEquals("Pizza", response.getBody().get(0).getName());
    }

    @Test
    void updateProduct_shouldUpdateFieldsAndReturnProduct() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old Name");

        Product updatedData = new Product();
        updatedData.setName("New Name");
        updatedData.setPrice(new BigDecimal("30.00"));
        updatedData.setDescription("Nova descrição");
        updatedData.setType(ProductType.DRINK);

        when(productRepository.getReferenceById(1L)).thenReturn(existing);

        ResponseEntity<Product> response = productService.updateProduct(1L, updatedData);

        assertEquals("New Name", response.getBody().getName());
        assertEquals("Nova descrição", response.getBody().getDescription());
        assertEquals(ProductType.DRINK, response.getBody().getType());
        assertEquals(new BigDecimal("30.00"), response.getBody().getPrice());

        verify(productRepository).save(existing);
    }

    @Test
    void deleteProduct_shouldDeleteProductAndDependencies() {
        Long productId = 1L;

        doNothing().when(itemRepository).deleteByProductId(productId);
        doNothing().when(productRepository).deleteById(productId);

        ResponseEntity<?> response = productService.deleteProduct(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody());

        verify(itemRepository).deleteByProductId(productId);
        verify(productRepository).deleteById(productId);
    }
}