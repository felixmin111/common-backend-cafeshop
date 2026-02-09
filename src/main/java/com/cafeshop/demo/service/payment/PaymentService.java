package com.cafeshop.demo.service.payment;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mapper.PaymentMapper;
import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import com.cafeshop.demo.repository.PaymentRepository;
import com.cafeshop.demo.service.payment.creator.InvoiceCreator;
import com.cafeshop.demo.service.payment.creator.OrderCreator;
import com.cafeshop.demo.service.payment.processor.PaymentProcessor;
import com.cafeshop.demo.service.payment.processor.PaymentProcessorResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final InvoiceCreator invoiceCreator;
    private final OrderCreator orderCreator;
    private final PaymentFactory paymentFactory;
    private final PaymentProcessorResolver processorResolver;
    private final PaymentRepository paymentRepo;
    private final PaymentMapper paymentMapper;

    public PaymentResponse create(PaymentCreateRequest req) {

        Invoice invoice = invoiceCreator.createInvoice(req);
        orderCreator.createOrders(invoice, req.getItems());

        Payment payment = paymentFactory.createPayment(invoice, req);
        PaymentProcessor processor = processorResolver.resolve(payment.getMethod());

        processor.process(payment, req);
        Payment saved = paymentRepo.save(payment);
        return paymentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        return paymentRepo.findWithDetailsById(id)
                .map(paymentMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
    }

    @Transactional
    public void updateStatusByGatewayPaymentId(String chargeId,
                                               PaymentStatus newStatus,
                                               String rawCallback) {

        Payment payment = paymentRepo.findByGatewayPaymentId(chargeId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for chargeId: " + chargeId));

        // prevent downgrading paid -> pending, etc. (optional rule)
        if (payment.getStatus() == PaymentStatus.PAID && newStatus != PaymentStatus.REFUNDED) {
            log.info("Skip status update: payment already PAID | paymentId={} chargeId={} newStatus={}",
                    payment.getId(), chargeId, newStatus);
            return;
        }

        payment.setStatus(newStatus);
        payment.setRawCallback(rawCallback);

        // set paidAt only once
        if (newStatus == PaymentStatus.PAID && payment.getPaidAt() == null) {
            payment.setPaidAt(OffsetDateTime.now());
        }

        // Update invoice status if exists
        Invoice invoice = payment.getInvoice(); // make sure Payment has invoice relation
        if (invoice != null) {
            switch (newStatus) {
                case PAID -> {
                    invoice.setStatus("PAID"); // or InvoiceStatus enum
                }
                case PaymentStatus.FAILED, PaymentStatus.CANCELED -> {
                    invoice.setStatus("PAYMENT_FAILED");
                }
                case REFUNDED -> {
                    invoice.setStatus("REFUNDED");
                    invoice.setRefundedAt(OffsetDateTime.now());
                }
                default -> {
                }
            }
        }
    }
}

