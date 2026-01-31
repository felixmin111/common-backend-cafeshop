package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.dto.payment.PaymentStatusUpdateRequest;
import com.cafeshop.demo.mapper.PaymentMapper;
import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import com.cafeshop.demo.repository.OrderRepository;
import com.cafeshop.demo.repository.PaymentRepository;
import com.cafeshop.demo.utils.PromptPayQrUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderPlaceRepository orderPlaceRepo;
    private final OrderRepository orderRepo;
    private final PaymentMapper paymentMapper;

    public PaymentResponse create(PaymentCreateRequest req) {
        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + req.getOrderPlaceId()));

        Order order = null;
        if (req.getOrderId() != null) {
            order = orderRepo.findById(req.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + req.getOrderId()));
        }

        PaymentMethod method = req.getMethod() == null ? PaymentMethod.PROMPTPAY_QR : req.getMethod();

        Payment payment = Payment.builder()
                .orderPlace(place)
                .order(order)
                .amount(req.getAmount())
                .method(method)
                .status(PaymentStatus.PENDING)
                .gateway(req.getGateway())
                .gatewayPaymentId(req.getGatewayPaymentId())
                .referenceNo("PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .build();

        if (method == PaymentMethod.PROMPTPAY_QR) {
            if (req.getPromptPayId() == null || req.getPromptPayId().isBlank()) {
                throw new IllegalArgumentException("promptPayId is required for PROMPTPAY_QR");
            }
            payment.setPromptPayId(req.getPromptPayId());
            payment.setQrPayload(PromptPayQrUtils.buildPayload(req.getPromptPayId(), req.getAmount()));
        }

        return paymentMapper.toResponse(paymentRepo.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        return paymentMapper.toResponse(paymentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByOrderPlace(Long orderPlaceId) {
        return paymentRepo.findByOrderPlace_Id(orderPlaceId).stream()
                .map(paymentMapper::toResponse).toList();
    }

    public PaymentResponse updateStatus(Long id, PaymentStatusUpdateRequest req) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));

        payment.setStatus(req.getStatus());
        return paymentMapper.toResponse(payment);
    }
    @Transactional
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
}
