package com.cafeshop.demo.service.webhook;

import com.cafeshop.demo.dto.webhook.OmiseWebhookEvent;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class OmiseWebhookParser {

    private static final ObjectMapper OM = new ObjectMapper();

    public OmiseWebhookEvent parse(String payload) {
        try {
            JsonNode root = OM.readTree(payload);

            String chargeId = root.path("data").path("id").asText(null);
            if (chargeId == null) chargeId = root.path("charge").path("id").asText(null);

            String status = root.path("data").path("status").asText(null);
            if (status == null) status = root.path("charge").path("status").asText(null);

            if (chargeId == null || chargeId.isBlank()) {
                throw new IllegalArgumentException("charge id not found in webhook payload");
            }
            if (status == null || status.isBlank()) {
                throw new IllegalArgumentException("status not found in webhook payload");
            }

            return new OmiseWebhookEvent(chargeId, status);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse webhook payload: " + e.getMessage(), e);
        }
    }
}
