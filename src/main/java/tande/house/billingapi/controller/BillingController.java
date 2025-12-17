package tande.house.billingapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tande.house.billingapi.dto.*;
import tande.house.billingapi.model.Boleta;
import tande.house.billingapi.service.BillingService;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @Value("${internal.serviceKey}")
    private String internalKey;

    private void checkKey(String key) {
        if (internalKey != null && !internalKey.isBlank()) {
            if (!internalKey.equals(key))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/boletas/fromCommerceOrder")
    public BoletaResponse create(
            @RequestHeader("X-Internal-Key") String key,
            @Valid @RequestBody CreateBoletaRequest req
    ) {
        checkKey(key);

        Boleta b = billingService.create(
                req.getCommerceOrder(),
                req.getTotal()
        );

        return new BoletaResponse(
                b.getCommerceOrder(),
                b.getFolio(),
                b.getNeto(),
                b.getIva(),
                b.getTotal(),
                b.getCreatedAt()
        );
    }

    @GetMapping("/boletas/byCommerceOrder/{commerceOrder}")
    public BoletaResponse get(@PathVariable String commerceOrder) {
        Boleta b = billingService.getByCommerceOrder(commerceOrder);

        return new BoletaResponse(
                b.getCommerceOrder(),
                b.getFolio(),
                b.getNeto(),
                b.getIva(),
                b.getTotal(),
                b.getCreatedAt()
        );
    }
}
