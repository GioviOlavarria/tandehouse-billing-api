package tande.house.billingapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateBoletaRequest {

    @NotBlank
    private String commerceOrder;

    @Positive
    private int total;
}
