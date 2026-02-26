package com.cafeshop.demo.controller;

import com.cafeshop.demo.service.webSocket.PaymentEventPublisher;
import com.cafeshop.demo.service.webhook.OmiseSignatureVerifier;
import com.cafeshop.demo.service.webhook.OmiseWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OmiseWebhookController {
    private final PaymentEventPublisher eventPublisher;
    private final OmiseWebhookService omiseWebhookService;
    private final OmiseSignatureVerifier verifier;

    @PostMapping(value = "/payments/webhook/omise", consumes = "application/json")
    public ResponseEntity<Void> webhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "Omise-Signature", required = false) String signature,
            @RequestHeader(value = "Omise-Signature-Timestamp", required = false) String timestamp
    ) {
        verifier.verify(rawBody, signature, timestamp);
        omiseWebhookService.handle(rawBody);

        return ResponseEntity.ok().build();
    }
}
