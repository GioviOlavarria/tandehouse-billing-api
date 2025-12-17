package tande.house.billingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tande.house.billingservice.dto.*;
import tande.house.billingservice.model.*;
import tande.house.billingservice.repo.*;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final ProductClient productClient;

    @Value("${internal.serviceKey}")
    private String internalKey;

    private String newCommerceOrder() {
        return "TH-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    @Transactional
    public CreateOrderResponse create(CreateOrderRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        String commerceOrder = newCommerceOrder();

        if (req.getCart() == null || req.getCart().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carrito vacío");
        }

        List<Map<String, Object>> stockItems = new ArrayList<>();
        List<OrderItemEntity> itemsToSave = new ArrayList<>();

        int total = 0;

        for (CartItemRequest ci : req.getCart()) {
            if (ci.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida");
            }

            Map<String, Object> p = productClient.getProduct(ci.getProductId());
            if (p == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no existe");

            boolean activo = Boolean.parseBoolean(String.valueOf(p.get("activo")));
            if (!activo) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto inactivo");

            int precio = Integer.parseInt(String.valueOf(p.get("precio")));
            String nombre = String.valueOf(p.get("nombre"));

            total += precio * ci.getQuantity();

            Map<String, Object> m = new HashMap<>();
            m.put("productId", ci.getProductId());
            m.put("quantity", ci.getQuantity());
            stockItems.add(m);

            OrderItemEntity item = new OrderItemEntity();
            item.setProductId(ci.getProductId());
            item.setNombreSnapshot(nombre);
            item.setPrecioSnapshot(precio);
            item.setQuantity(ci.getQuantity());
            itemsToSave.add(item);
        }

        Map<String, Object> stockPayload = Map.of(
                "commerceOrder", commerceOrder,
                "items", stockItems
        );

        try {
            productClient.reserveStock(internalKey, stockPayload);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo reservar stock");
        }

        OrderEntity order = new OrderEntity();
        order.setCommerceOrder(commerceOrder);
        order.setUserId(req.getUserId());
        order.setEmail(email);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(total);
        order.setCreatedAt(OffsetDateTime.now());
        order = orderRepo.save(order);

        for (OrderItemEntity it : itemsToSave) {
            it.setOrderId(order.getId());
        }
        itemRepo.saveAll(itemsToSave);

        return new CreateOrderResponse(order.getId(), order.getCommerceOrder(), order.getTotalAmount());
    }

    @Transactional
    public void markPaid(String commerceOrder) {
        OrderEntity order = orderRepo.findByCommerceOrder(commerceOrder)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order no existe"));

        if (order.getStatus() == OrderStatus.PAID) return;

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order no está pendiente");
        }

        List<OrderItemEntity> items = itemRepo.findByOrderId(order.getId());
        List<Map<String, Object>> stockItems = items.stream()
                .map(i -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("productId", i.getProductId());
                    m.put("quantity", i.getQuantity());
                    return m;
                })
                .toList();

        Map<String, Object> payload = Map.of("commerceOrder", commerceOrder, "items", stockItems);

        try {
            productClient.commitStock(internalKey, payload);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo confirmar stock");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepo.save(order);
    }

    @Transactional
    public void markFailed(String commerceOrder) {
        OrderEntity order = orderRepo.findByCommerceOrder(commerceOrder)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order no existe"));

        if (order.getStatus() == OrderStatus.CANCELLED) return;

        if (order.getStatus() == OrderStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order ya está pagada");
        }

        List<OrderItemEntity> items = itemRepo.findByOrderId(order.getId());
        List<Map<String, Object>> stockItems = items.stream()
                .map(i -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("productId", i.getProductId());
                    m.put("quantity", i.getQuantity());
                    return m;
                })
                .toList();

        Map<String, Object> payload = Map.of("commerceOrder", commerceOrder, "items", stockItems);

        try {
            productClient.releaseStock(internalKey, payload);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo liberar stock");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        OrderEntity order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order no existe"));

        List<OrderItemEntity> items = itemRepo.findByOrderId(order.getId());
        return toResponse(order, items);
    }

    @Transactional(readOnly = true)
    public OrderResponse getByCommerceOrder(String commerceOrder) {
        OrderEntity order = orderRepo.findByCommerceOrder(commerceOrder)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order no existe"));

        List<OrderItemEntity> items = itemRepo.findByOrderId(order.getId());
        return toResponse(order, items);
    }

    private OrderResponse toResponse(OrderEntity o, List<OrderItemEntity> items) {
        List<OrderItemResponse> respItems = items.stream()
                .map(i -> new OrderItemResponse(
                        i.getProductId(),
                        i.getNombreSnapshot(),
                        i.getPrecioSnapshot(),
                        i.getQuantity(),
                        i.getPrecioSnapshot() * i.getQuantity()
                ))
                .toList();

        return new OrderResponse(
                o.getId(),
                o.getCommerceOrder(),
                o.getUserId(),
                o.getEmail(),
                o.getStatus().name(),
                o.getTotalAmount(),
                o.getCreatedAt(),
                respItems
        );
    }
}
