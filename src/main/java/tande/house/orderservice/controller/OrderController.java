package tande.house.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tande.house.orderservice.dto.*;
import tande.house.orderservice.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Value("${internal.serviceKey}")
    private String internalKey;

    private void checkInternalKey(String key) {
        if (internalKey != null && !internalKey.isBlank()) {
            if (key == null || !internalKey.equals(key)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/orders")
    public CreateOrderResponse create(
            @RequestHeader(value = "X-Internal-Key", required = false) String key,
            @Valid @RequestBody CreateOrderRequest req
    ) {
        checkInternalKey(key);
        return orderService.create(req);
    }

    @PostMapping("/orders/markPaid")
    public void markPaid(
            @RequestHeader(value = "X-Internal-Key", required = false) String key,
            @Valid @RequestBody MarkRequest req
    ) {
        checkInternalKey(key);
        orderService.markPaid(req.getCommerceOrder());
    }

    @PostMapping("/orders/markFailed")
    public void markFailed(
            @RequestHeader(value = "X-Internal-Key", required = false) String key,
            @Valid @RequestBody MarkRequest req
    ) {
        checkInternalKey(key);
        orderService.markFailed(req.getCommerceOrder());
    }

    @GetMapping("/orders/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @GetMapping("/orders/byCommerceOrder/{commerceOrder}")
    public OrderResponse getByCommerceOrder(@PathVariable String commerceOrder) {
        return orderService.getByCommerceOrder(commerceOrder);
    }
}
