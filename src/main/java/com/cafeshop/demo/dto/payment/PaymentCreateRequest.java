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

    @NotNull
    private Long orderPlaceId;

    // optional: pay for a single order
    private Long orderId;

    @NotNull
    private BigDecimal amount;

    // default PROMPTPAY_QR
    private PaymentMethod method;

    // required only for PROMPTPAY_QR (phone or id)
    private String promptPayId;

    private String gatewayPaymentId;

    private String gateway;


}
