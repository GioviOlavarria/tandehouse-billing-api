package tande.house.orderservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tande.house.orderservice.model.OrderItemEntity;


import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrderId(Long orderId);
}