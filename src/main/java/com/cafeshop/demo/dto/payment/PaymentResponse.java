package com.cafeshop.demo.dto.payment;

import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderPlaceId;
    private Long orderId;

    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;

    private String promptPayId;
    private String qrPayload;
    private String referenceNo;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
