package com.cafeshop.demo.controller;

import com.cafeshop.demo.service.webhook.OmiseWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OmiseWebhookController {

    private final OmiseWebhookService omiseWebhookService;

    @PostMapping("/payments/webhook/omise")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Omise-Signature", required = false) String omiseSig,
            @RequestHeader(value = "Omise-Signature-Timestamp", required = false) String omiseTs,
            @RequestHeader(value = "X-Omise-Signature", required = false) String xSig,
            @RequestHeader(value = "X-Omise-Signature-Timestamp", required = false) String xTs
    ) {
        String signature = (omiseSig != null) ? omiseSig : xSig;
        String timestamp = (omiseTs != null) ? omiseTs : xTs;

        omiseWebhookService.handle(payload, signature, timestamp);
        return ResponseEntity.ok().build();
    }
}
