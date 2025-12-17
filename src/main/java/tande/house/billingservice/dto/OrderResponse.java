package tande.house.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String commerceOrder;
    private Long userId;
    private String email;
    private String status;
    private int totalAmount;
    private OffsetDateTime createdAt;
    private List<OrderItemResponse> items;
}
