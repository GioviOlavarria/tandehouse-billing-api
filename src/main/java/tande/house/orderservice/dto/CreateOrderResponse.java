package tande.house.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {
    private Long orderId;
    private String commerceOrder;
    private int amount;
}
