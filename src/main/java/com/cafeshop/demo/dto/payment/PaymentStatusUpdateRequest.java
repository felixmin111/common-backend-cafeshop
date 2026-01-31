package com.cafeshop.demo.dto.payment;

import com.cafeshop.demo.mode.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusUpdateRequest {
    @NotNull
    private PaymentStatus status;
}
