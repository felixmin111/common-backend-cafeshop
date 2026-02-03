package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mapper.PaymentMapper;
import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import com.cafeshop.demo.repository.OrderRepository;
import com.cafeshop.demo.repository.PaymentRepository;
import com.cafeshop.demo.service.omise.OmiseGatewayClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderPlaceRepository orderPlaceRepo;
    private final OrderRepository orderRepo;
    private final PaymentMapper paymentMapper;
    private final OmiseGatewayClient omiseGateway;

    public PaymentResponse create(PaymentCreateRequest req) {
        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + req.getOrderPlaceId()));

        Order order = null;
        if (req.getOrderId() != null) {
            order = orderRepo.findById(req.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + req.getOrderId()));
        }

        PaymentMethod method = req.getMethod() == null ? PaymentMethod.PROMPTPAY_QR : req.getMethod();
        String gateway = req.getGateway();

        String referenceNo = "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Payment payment = Payment.builder()
                .orderPlace(place)
                .order(order)
                .amount(req.getAmount())
                .method(method)
                .status(PaymentStatus.PENDING)
                .gateway(gateway)
                .referenceNo(referenceNo)
                .build();

        if (method != PaymentMethod.PROMPTPAY_QR) {
            throw new IllegalArgumentException("Only PROMPTPAY_QR supported in this example");
        }

        // ✅ Omise integration
        if (!"OMISE".equalsIgnoreCase(gateway)) {
            throw new IllegalArgumentException("gateway must be OMISE");
        }

        long amountSatang = toSatang(req.getAmount());

        var result = omiseGateway.createPromptPayCharge(amountSatang, "THB", referenceNo);

        payment.setGatewayPaymentId(result.chargeId()); // server-generated
        payment.setQrPayload(result.qrPayload());
        payment.setQrImageUrl(result.qrImageUrl());

        if (req.getPromptPayId() != null && !req.getPromptPayId().isBlank()) {
            payment.setPromptPayId(req.getPromptPayId());
        }

        return paymentMapper.toResponse(paymentRepo.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        return paymentMapper.toResponse(paymentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id)));
    }

    public void updateStatusByGatewayPaymentId(String chargeId,
                                               PaymentStatus newStatus,
                                               String rawCallback) {
        Payment payment = paymentRepo.findByGatewayPaymentId(chargeId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for chargeId: " + chargeId));

        payment.setStatus(newStatus);
        payment.setRawCallback(rawCallback);

        if (newStatus == PaymentStatus.PAID && payment.getPaidAt() == null) {
            payment.setPaidAt(OffsetDateTime.now());
        }
    }

    private long toSatang(BigDecimal thb) {
        // 250.00 -> 25000
        return thb.movePointRight(2).setScale(0, BigDecimal.ROUND_HALF_UP).longValueExact();
    }
}
