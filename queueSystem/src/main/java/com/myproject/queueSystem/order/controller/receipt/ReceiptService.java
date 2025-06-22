package com.myproject.queueSystem.order.controller.receipt;

import com.myproject.queueSystem.order.domain.order.Order;
import com.myproject.queueSystem.order.domain.order.OrderDTO;
import com.myproject.queueSystem.order.domain.order.OrderRepository;
import com.myproject.queueSystem.order.domain.order.OrderService;
import com.myproject.queueSystem.order.domain.order.item.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptService {

    @Autowired
    private OrderRepository orderRepository;

    public ResponseEntity<ReceiptDTO> returnReceipt(Long id) {
        Order order = orderRepository.getReferenceById(id);

        List<ItemDTO> itemDTOList = order.getItems().stream()
                .map(item -> new ItemDTO(
                        item.getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getNotes(),
                        item.getProduct().getId(),
                        item.getPrice(),
                        null,
                        item.getExtraList()
                ))
                .toList();

        ReceiptDTO receiptDTO = new ReceiptDTO(
                order.getId(),
                itemDTOList,
                order.getTotal(),
                order.getQueue().getCode(), // Ou outro código de identificação
                order.getOpenedAt()
        );

        return ResponseEntity.ok(receiptDTO);
    }

}
