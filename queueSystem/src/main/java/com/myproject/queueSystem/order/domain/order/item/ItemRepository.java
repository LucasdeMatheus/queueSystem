package com.myproject.queueSystem.order.domain.order.item;

import com.myproject.queueSystem.order.domain.analytics.TopProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndOrderId(Long itemId, Long orderId);

    void deleteByIdAndOrderId(Long itemId, Long orderId);

    @Modifying
    void deleteByProductId(Long id);

    @Query("""
    SELECT new com.myproject.queueSystem.order.domain.analytics.TopProductDTO(i.product, SUM(i.quantity))
    FROM Item i
    WHERE i.order.status = com.myproject.queueSystem.order.domain.order.STATUS.CLOSED
      AND (:startDate IS NULL OR i.order.openedAt >= :startDate)
    GROUP BY i.product
    ORDER BY SUM(i.quantity) DESC
""")
    List<TopProductDTO> findProductsMostSold(@Param("startDate") LocalDateTime startDate);


}
