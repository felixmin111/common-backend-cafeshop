package com.cafeshop.demo.dto.payment;

import com.cafeshop.demo.mode.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateRequest {

    @NotNull private Long orderPlaceId;
    private Long orderId;

    @NotNull private BigDecimal amount;

    private PaymentMethod method;   // PROMPTPAY_QR
    private String promptPayId;

    @NotNull
    private String gateway; // "OMISE"
}
