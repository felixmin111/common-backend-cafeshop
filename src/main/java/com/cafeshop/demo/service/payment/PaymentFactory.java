package com.cafeshop.demo.service.payment;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentFactory {

    public Payment createPayment(Invoice invoice, PaymentCreateRequest req) {

        PaymentMethod method =
                req.getMethod() == null ? PaymentMethod.PROMPTPAY_QR : req.getMethod();

        return Payment.builder()
                .invoice(invoice)
                .orderPlace(invoice.getOrderPlace())
                .amount(invoice.getGrandTotal())
                .method(method)
                .status(PaymentStatus.PENDING)
                .gateway(req.getGateway())
                .referenceNo("PAY-" + UUID.randomUUID().toString().substring(0, 16))
                .build();
    }
}
