package tande.house.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_items_order_id", columnList = "orderId")
})
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String nombreSnapshot;

    @Column(nullable = false)
    private int precioSnapshot;

    @Column(nullable = false)
    private int quantity;
}
