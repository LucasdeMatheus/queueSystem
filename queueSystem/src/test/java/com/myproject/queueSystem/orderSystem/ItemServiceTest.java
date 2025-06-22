package com.myproject.queueSystem.orderSystem;

import com.myproject.queueSystem.order.domain.order.Order;
import com.myproject.queueSystem.order.domain.order.OrderRepository;
import com.myproject.queueSystem.order.domain.order.item.*;
import com.myproject.queueSystem.order.domain.product.Product;
import com.myproject.queueSystem.order.domain.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemService itemService;
    private ItemRepository itemRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);

        itemService = new ItemService();
        itemService.itemRepository = itemRepository;
        itemService.orderRepository = orderRepository;
        itemService.productRepository = productRepository;
    }

    @Test
    void addItem_shouldAddItemToOrderAndReturnDTO() {
        Long orderId = 1L;
        Long productId = 1L;
        Long extraId1 = 2L;
        Long extraId2 = 3L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Pizza");
        product.setPrice(new BigDecimal("20.00"));

        Product extra1 = new Product();
        extra1.setId(extraId1);
        extra1.setName("Queijo");
        extra1.setPrice(new BigDecimal("5.00"));

        Product extra2 = new Product();
        extra2.setId(extraId2);
        extra2.setName("Bacon");
        extra2.setPrice(new BigDecimal("3.00"));

        Order order = new Order();
        order.setId(orderId);

        ItemDTO itemDTO = new ItemDTO(
                null, "Pizza", 1, "Sem cebola", productId,
                null, List.of(extraId1, extraId2), null
        );

        when(orderRepository.getReferenceById(orderId)).thenReturn(order);
        when(productRepository.getReferenceById(productId)).thenReturn(product);
        when(productRepository.findById(extraId1)).thenReturn(Optional.of(extra1));
        when(productRepository.findById(extraId2)).thenReturn(Optional.of(extra2));

        ResponseEntity<ItemDTO> response = itemService.addItem(orderId, itemDTO);

        assertEquals("Pizza", response.getBody().name());
        assertEquals(new BigDecimal("28.00"), response.getBody().price()); // 20 + 5 + 3
        assertEquals(2, response.getBody().extras().size());

        verify(itemRepository).save(any(Item.class));
        verify(orderRepository).save(order);
    }
    @Test
    void updateItem_shouldUpdateItemDetailsAndReturnDTO() {
        Long orderId = 1L;
        Long itemId = 10L;
        Long productId = 1L;
        Long extraId1 = 2L;
        Long extraId2 = 3L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Pizza");
        product.setPrice(new BigDecimal("20.00"));

        Product extra1 = new Product();
        extra1.setId(extraId1);
        extra1.setName("Queijo");
        extra1.setPrice(new BigDecimal("5.00"));

        Product extra2 = new Product();
        extra2.setId(extraId2);
        extra2.setName("Bacon");
        extra2.setPrice(new BigDecimal("3.00"));

        Order order = new Order();
        order.setId(orderId);
        order.setItems(new ArrayList<>());
        order.setTotal(new BigDecimal("0.00"));

        Item item = new Item();
        item.setId(itemId);
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(1);
        item.setNotes("Sem nada");
        item.setPrice(new BigDecimal("20.00"));

        ItemDTO updateDTO = new ItemDTO(
                null,
                "Pizza",
                2,  // nova quantidade
                "Com cebola",
                productId,
                null,
                List.of(extraId1, extraId2),
                null
        );

        when(itemRepository.findByIdAndOrderId(itemId, orderId)).thenReturn(Optional.of(item));
        when(productRepository.findById(extraId1)).thenReturn(Optional.of(extra1));
        when(productRepository.findById(extraId2)).thenReturn(Optional.of(extra2));

        ResponseEntity<ItemDTO> response = itemService.updateItem(orderId, itemId, updateDTO);

        assertEquals("Pizza", response.getBody().name());
        assertEquals(2, response.getBody().quantity());
        assertEquals("Com cebola", response.getBody().notes());
        assertEquals(new BigDecimal("56.00"), response.getBody().price()); // (20 + 5 + 3) * 2
        assertNotNull(response.getBody().extras());

        verify(itemRepository).save(item);
    }
    @Test
    void deleteItem_shouldDeleteItemSuccessfully() {
        Long orderId = 1L;
        Long itemId = 10L;

        doNothing().when(itemRepository).deleteByIdAndOrderId(itemId, orderId);

        ResponseEntity<?> response = itemService.deleteItem(orderId, itemId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Item deleted successfully.", response.getBody());

        verify(itemRepository).deleteByIdAndOrderId(itemId, orderId);
    }

}
