package com.cafeshop.demo.service.payment.processor;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentMethod;

public interface PaymentProcessor {
    PaymentMethod supports();
    void process(Payment payment, PaymentCreateRequest req);
}
