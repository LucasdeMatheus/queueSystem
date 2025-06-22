package com.myproject.queueSystem.order.domain.order.item;

import com.myproject.queueSystem.order.domain.order.Order;
import com.myproject.queueSystem.order.domain.order.OrderRepository;
import com.myproject.queueSystem.order.domain.product.Product;
import com.myproject.queueSystem.order.domain.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<ItemDTO> addItem(Long orderId, ItemDTO data) {
        Order order = orderRepository.getReferenceById(orderId);
        Product product = productRepository.getReferenceById(data.idProduct());

        Item item = new Item();
        item.setOrder(order);
        item.setProduct(product);

        int quantity = (data.quantity() != null && data.quantity() > 0) ? data.quantity() : 1;
        item.setQuantity(quantity);

        BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;

        List<Product> extraProducts = new ArrayList<>();
        if (data.extras() != null && !data.extras().isEmpty()) {
            for (Long extraId : data.extras()) {
                Product extra = productRepository.findById(extraId)
                        .orElseThrow(() -> new RuntimeException("Extra product with id " + extraId + " not found"));
                extraProducts.add(extra);
                price = price.add(extra.getPrice() != null ? extra.getPrice() : BigDecimal.ZERO);
            }
            item.setExtraList(extraProducts);
        } else {
            item.setExtraList(null);
        }

        item.setPrice(price.multiply(BigDecimal.valueOf(quantity)));
        item.setNotes(data.notes());

        if (order.getTotal() == null) order.setTotal(BigDecimal.ZERO);
        order.setTotal(order.getTotal().add(item.getPrice()));

        order.getItems().add(item);
        itemRepository.save(item);
        orderRepository.save(order);

        ItemDTO itemDTO = new ItemDTO(
                item.getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getNotes(),
                item.getProduct().getId(),
                item.getPrice(),
                data.extras(), // IDs enviados
                item.getExtraList() // lista completa
        );

        return ResponseEntity.ok(itemDTO);
    }



    public ResponseEntity<ItemDTO> updateItem(Long orderId, Long itemId, ItemDTO data) {
        Item item = itemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new RuntimeException("Item not found or does not belong to this order"));

        BigDecimal price = item.getProduct().getPrice() != null ? item.getProduct().getPrice() : BigDecimal.ZERO;

        List<Product> extraProducts = new ArrayList<>();
        if (data.extras() != null && !data.extras().isEmpty()) {
            for (Long extraId : data.extras()) {
                Product extra = productRepository.findById(extraId)
                        .orElseThrow(() -> new RuntimeException("Extra product with id " + extraId + " not found"));
                extraProducts.add(extra);
                if (extra.getPrice() != null) {
                    price = price.add(extra.getPrice());
                }
            }
            item.setExtraList(extraProducts);
        } else {
            item.setExtraList(null);
        }

        item.setPrice(price.multiply(BigDecimal.valueOf(data.quantity() == null ? 1 : data.quantity())));

        if (data.quantity() != null) {
            item.setQuantity(data.quantity());
        }
        if (data.notes() != null) {
            item.setNotes(data.notes());
        }

        itemRepository.save(item);

        ItemDTO itemDTO = new ItemDTO(
                item.getOrder().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getNotes(),
                item.getProduct().getId(),
                item.getPrice(),
                null,
                item.getExtraList()
        );
        return ResponseEntity.ok(itemDTO);
    }

    @Transactional
    public ResponseEntity<?> deleteItem(Long orderId, Long itemId) {
        try {
            itemRepository.deleteByIdAndOrderId(itemId, orderId);
            return ResponseEntity.ok("Item deleted successfully.");
        } catch (Exception e) {
        e.printStackTrace(); // Adicione isso
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while deleting the item.");
    }

}


}
