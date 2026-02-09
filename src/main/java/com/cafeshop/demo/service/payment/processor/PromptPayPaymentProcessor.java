package com.cafeshop.demo.service.payment.processor;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.service.omise.OmiseGatewayClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptPayPaymentProcessor implements PaymentProcessor {

    private final OmiseGatewayClient omiseGateway;

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.PROMPTPAY_QR;
    }

    @Override
    public void process(Payment payment, PaymentCreateRequest req) {

        long satang = payment.getAmount()
                .movePointRight(2)
                .longValueExact();

        var result = omiseGateway.createPromptPayCharge(
                satang, "THB", payment.getReferenceNo());

        payment.setGatewayPaymentId(result.chargeId());
        payment.setQrPayload(result.qrPayload());
        payment.setQrImageUrl(result.qrImageUrl());
        payment.setPromptPayId(req.getPromptPayId());
    }
}

