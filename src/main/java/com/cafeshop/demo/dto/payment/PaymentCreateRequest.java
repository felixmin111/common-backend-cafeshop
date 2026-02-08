package com.cafeshop.demo.dto.payment;

import com.cafeshop.demo.dto.order.OrderRequest;
import com.cafeshop.demo.mode.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentCreateRequest {

    @NotNull
    private Long orderPlaceId;

    @NotBlank
    private String customerName;

    @NotNull
    @Valid
    private List<OrderRequest> items;

    // optional: if you want client send amount, otherwise compute server-side
    private BigDecimal amount;

    @Builder.Default
    private PaymentMethod method = PaymentMethod.PROMPTPAY_QR; // or CASH

    private String promptPayId;

    @NotNull
    private String gateway; // "OMISE" (only required for PROMPTPAY_QR)
}
