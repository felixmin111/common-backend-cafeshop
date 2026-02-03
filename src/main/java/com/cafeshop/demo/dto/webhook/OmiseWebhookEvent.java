package com.cafeshop.demo.dto.webhook;


public record OmiseWebhookEvent(
        String eventId,
        String key,
        String chargeId,
        String status,
        Long amount,
        String currency
) {}