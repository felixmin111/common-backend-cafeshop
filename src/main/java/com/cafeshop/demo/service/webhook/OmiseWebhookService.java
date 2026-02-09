package com.cafeshop.demo.service.webhook;

import com.cafeshop.demo.dto.webhook.OmiseWebhookEvent;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import com.cafeshop.demo.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OmiseWebhookService {
    private final OmiseWebhookParser parser;
    private final PaymentService paymentService;

    @Transactional
    public void handle(String payload) {
        OmiseWebhookEvent event = parser.parse(payload);

        // Only process charge events (optional safety)
        if (event.chargeId() == null || event.status() == null) return;

        PaymentStatus newStatus = mapStatus(event.status());

        paymentService.updateStatusByGatewayPaymentId(
                event.chargeId(),
                newStatus,
                payload
        );
    }

    private PaymentStatus mapStatus(String omiseStatus) {
        return switch (omiseStatus) {
            case "successful" -> PaymentStatus.PAID;
            case "failed"     -> PaymentStatus.FAILED;
            case "pending"    -> PaymentStatus.PENDING;
            default           -> PaymentStatus.PENDING;
        };
    }
}
