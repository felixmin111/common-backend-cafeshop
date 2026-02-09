package com.cafeshop.demo.service.payment.processor;

import com.cafeshop.demo.mode.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProcessorResolver {

    private final List<PaymentProcessor> processors;

    public PaymentProcessor resolve(PaymentMethod method) {
        return processors.stream()
                .filter(p -> p.supports() == method)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported method: " + method));
    }
}
