package tande.house.billingservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CreateOrderRequest {
    private Long userId;

    @NotBlank @Email
    private String email;

    @Valid
    @NotEmpty
    private List<CartItemRequest> cart;
}
