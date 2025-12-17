package tande.house.orderservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tande.house.orderservice.model.OrderEntity;


import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByCommerceOrder(String commerceOrder);
}
