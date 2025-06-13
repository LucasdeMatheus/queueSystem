package com.myproject.queueSystem.order.domain.order.item;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndOrderId(Long itemId, Long orderId);

    void deleteByIdAndOrderId(Long itemId, Long orderId);

    @Modifying
    void deleteByProductId(Long id);
}
