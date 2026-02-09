package com.cafeshop.demo.service.payment.processor;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class CashPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.CASH;
    }

    @Override
    public void process(Payment payment, PaymentCreateRequest req) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaidAt(OffsetDateTime.now());
    }
}
