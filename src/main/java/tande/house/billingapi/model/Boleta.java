package tande.house.billingapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "boletas", indexes = {
        @Index(name = "idx_boleta_commerce_order", columnList = "commerceOrder", unique = true),
        @Index(name = "idx_boleta_folio", columnList = "folio", unique = true)
})
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String commerceOrder;

    @Column(nullable = false, unique = true)
    private Long folio;

    @Column(nullable = false)
    private int neto;

    @Column(nullable = false)
    private int iva;

    @Column(nullable = false)
    private int total;

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
