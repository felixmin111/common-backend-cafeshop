package com.cafeshop.demo.service.webhook;

import com.cafeshop.demo.dto.webhook.OmiseWebhookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OmiseWebhookParser {

    private final ObjectMapper objectMapper;

    public OmiseWebhookEvent parse(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);

            String eventId = text(root, "id");
            String key = text(root, "key");

            JsonNode data = root.path("data");
            String chargeId = text(data, "id");
            String status = text(data, "status");
            Long amount = data.path("amount").isNumber() ? data.get("amount").asLong() : null;
            String currency = text(data, "currency");

            return new OmiseWebhookEvent(eventId, key, chargeId, status, amount, currency);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid webhook payload: " + e.getMessage(), e);
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }
}
