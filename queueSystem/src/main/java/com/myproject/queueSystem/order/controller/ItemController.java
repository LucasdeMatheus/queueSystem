package com.myproject.queueSystem.order.controller;

import com.myproject.queueSystem.order.domain.order.item.ItemDTO;
import com.myproject.queueSystem.order.domain.order.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/{orderId}/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // Criar item
    @PostMapping
    public ResponseEntity<ItemDTO> addItem(@PathVariable Long orderId, @RequestBody ItemDTO data) {
        return itemService.addItem(orderId, data);
    }

    // Atualizar item
    @PutMapping("/{itemId}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long orderId, @PathVariable Long itemId, @RequestBody ItemDTO data) {
        return itemService.updateItem(orderId, itemId, data);
    }

    // Deletar item
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return itemService.deleteItem(orderId, itemId);
    }
}
