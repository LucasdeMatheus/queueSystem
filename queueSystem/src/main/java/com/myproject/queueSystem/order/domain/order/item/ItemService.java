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

        // Quantidade: usa 1 como padrão se for nulo ou menor que 1
        int quantity = (data.quantity() != null && data.quantity() > 0) ? data.quantity() : 1;
        item.setQuantity(quantity);

        // Começa com o preço do produto principal
        BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;

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

        // Multiplica o preço unitário pela quantidade
        item.setPrice(price.multiply(BigDecimal.valueOf(quantity)));

        // Notas opcionais
        item.setNotes(data.notes());

        // Atualiza total da ordem (opcional, mas útil se quiser manter a ordem sincronizada)
        if (order.getTotal() == null) {
            order.setTotal(BigDecimal.ZERO);
        }
        order.setTotal(order.getTotal().add(item.getPrice()));

        order.getItems().add(item);
        itemRepository.save(item); // importante salvar antes caso precise do ID
        orderRepository.save(order);
        List<Long> extraIds = null;
        if (item.getExtraList() != null) {
            extraIds = item.getExtraList().stream()
                    .map(Product::getId)
                    .toList();  // ou .collect(Collectors.toList())
        }

        ItemDTO itemDTO = new ItemDTO(item.getOrder().getId(),item.getQuantity(), item.getNotes(), item.getProduct().getId(), item.getExtraList(), null);
        return ResponseEntity.ok(itemDTO);
    }



    public ResponseEntity<ItemDTO> updateItem(Long orderId, Long itemId, ItemDTO data) {
        Item item = itemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new RuntimeException("Item not found or does not belong to this order"));

        BigDecimal price = item.getProduct().getPrice() != null ? item.getProduct().getPrice() : BigDecimal.ZERO;

        List<Product> extraProducts = item.getExtraList();
        if (data.extras() != null && !data.extras().isEmpty()) {
            for (Long extraId : data.extras()) {
                Product extra = productRepository.findById(extraId)
                        .orElseThrow(() -> new RuntimeException("Extra product with id " + extraId + " not found"));
                extraProducts.add(extra);
                if (extra.getPrice() != null) {
                    price = price.add(extra.getPrice());
                }
            }
        }else{
            extraProducts = null;
        }
        item.setExtraList(extraProducts);

        item.setPrice(price.multiply(BigDecimal.valueOf(data.quantity() == null ? 1 : data.quantity())));

        if (data.quantity() != null) {
            item.setQuantity(data.quantity());
        }
        if (data.notes() != null) {
            item.setNotes(data.notes());
        }

        itemRepository.save(item); // persiste a atualização
        ItemDTO itemDTO = new ItemDTO(item.getOrder().getId(),item.getQuantity(), item.getNotes(), item.getProduct().getId(), item.getExtraList(), null);
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
