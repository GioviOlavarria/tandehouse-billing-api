package tande.house.billingservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tande.house.billingservice.model.OrderItemEntity;


import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrderId(Long orderId);
}