package tande.house.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private Long productId;
    private String nombre;
    private int precio;
    private int quantity;
    private int lineTotal;
}