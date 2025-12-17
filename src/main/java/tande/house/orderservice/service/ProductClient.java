package tande.house.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient.Builder builder = WebClient.builder();

    @Value("${services.product}")
    private String productBase;

    public Map<String, Object> getProduct(Long productId) {
        return builder.build()
                .get()
                .uri(productBase + "/products/" + productId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public void reserveStock(String internalKey, Map<String, Object> payload) {
        builder.build()
                .post()
                .uri(productBase + "/stock/reserve")
                .header("X-Internal-Key", internalKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void commitStock(String internalKey, Map<String, Object> payload) {
        builder.build()
                .post()
                .uri(productBase + "/stock/commit")
                .header("X-Internal-Key", internalKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void releaseStock(String internalKey, Map<String, Object> payload) {
        builder.build()
                .post()
                .uri(productBase + "/stock/release")
                .header("X-Internal-Key", internalKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
