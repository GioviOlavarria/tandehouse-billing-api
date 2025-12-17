package tande.house.billingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class BoletaResponse {
    private String commerceOrder;
    private Long folio;
    private int neto;
    private int iva;
    private int total;
    private OffsetDateTime createdAt;
}
