package com.cafeshop.demo.dto.payment;

import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;

    private Long orderPlaceId;
    private Long orderId;

    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;

    private String gateway;
    private String gatewayPaymentId; // Omise charge id

    private String referenceNo;

    // QR info for client
    private String qrPayload;
    private String qrImageUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime paidAt;
}
