package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.order.OrderRequest;
import com.cafeshop.demo.dto.orderIngredient.OrderIngredientRequest;
import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mapper.PaymentMapper;
import com.cafeshop.demo.mode.*;
import com.cafeshop.demo.mode.enums.OrderStatus;
import com.cafeshop.demo.mode.enums.PaymentMethod;
import com.cafeshop.demo.mode.enums.PaymentStatus;
import com.cafeshop.demo.repository.*;
import com.cafeshop.demo.service.omise.OmiseGatewayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderPlaceRepository orderPlaceRepo;
    private final OrderRepository orderRepo;
    private final InvoiceRepository invoiceRepo;
    private final InvoiceOrderRepository invoiceOrderRepo;
    private final OrderIngredientRepository orderIngredientRepo;
    private final MenuItemSizeRepository menuItemSizeRepo;
    private final IngredientRepository ingredientRepo;

    private final PaymentMapper paymentMapper;
    private final OmiseGatewayClient omiseGateway;

    public PaymentResponse create(PaymentCreateRequest req) {
        System.out.println("req.getOrderPlaceId()-->"+req.getOrderPlaceId());
        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + req.getOrderPlaceId()));

        System.out.println("place-->"+place.toString());

        PaymentMethod method = (req.getMethod() == null) ? PaymentMethod.PROMPTPAY_QR : req.getMethod();

        // 1) Create Invoice first
        Invoice invoice = Invoice.builder()
                .orderPlace(place)
                .customerName(req.getCustomerName())
                .status("PENDING") // or enum
                .appliedAt(OffsetDateTime.now())
                .subTotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .build();
        invoice = invoiceRepo.save(invoice);

        BigDecimal subTotal = BigDecimal.ZERO;

        // 2) Create orders + order_ingredients + invoice_order
        for (OrderRequest itemReq : req.getItems()) {

            var mis = menuItemSizeRepo.findById(itemReq.getMenuItemSizeId())
                    .orElseThrow(() -> new IllegalArgumentException("MenuItemSize not found: " + itemReq.getMenuItemSizeId()));

            BigDecimal unitPrice = BigDecimal.valueOf(mis.getSellPrice()); // adjust getter name
            BigDecimal qty = BigDecimal.valueOf(itemReq.getQty());
            BigDecimal lineTotal = unitPrice.multiply(qty);

            // Order
            Order order = Order.builder()
                    .orderPlace(place)
                    .menuItemSize(mis)
                    .qty(itemReq.getQty())
                    .unitPrice(unitPrice)
                    .totalPrice(lineTotal)
                    .note(itemReq.getNote())
                    .status(OrderStatus.PENDING)
                    .customerName(null)
                    .build();
            order = orderRepo.save(order);

            // Order Ingredients (safe even if null)
            if (itemReq.getIngredients() != null && !itemReq.getIngredients().isEmpty()) {
                for (OrderIngredientRequest ingReq : itemReq.getIngredients()) {
                    var ingredient = ingredientRepo.findById(ingReq.getIngredientId())
                            .orElseThrow(() -> new IllegalArgumentException("Ingredient not found: " + ingReq.getIngredientId()));

                    OrderIngredient oi = OrderIngredient.builder()
                            .order(order)
                            .ingredient(ingredient)
                            .qty(ingReq.getQty())
                            .note(ingReq.getNote())
                            .build();

                    orderIngredientRepo.save(oi);
                }
            }

            InvoiceOrder io = InvoiceOrder.builder()
                    .invoice(invoice)
                    .order(order)
                    .menuItemName(mis.getMenuItem().getName())
                    .sizeName(mis.getSize().getName())
                    .qty(itemReq.getQty())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .note(itemReq.getNote())
                    .build();

            invoiceOrderRepo.save(io);

            subTotal = subTotal.add(lineTotal);
        }

        // 3) Update invoice totals
        invoice.setSubTotal(subTotal);

        // if you have tax/delivery logic:
        BigDecimal tax = invoice.getTax() == null ? BigDecimal.ZERO : invoice.getTax();
        BigDecimal delivery = invoice.getDeliveryFee() == null ? BigDecimal.ZERO : invoice.getDeliveryFee();

        invoice.setGrandTotal(subTotal.add(tax).add(delivery));
        invoiceRepo.save(invoice);

        // 4) Create Payment (linked to invoice)
        String referenceNo = "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Payment payment = Payment.builder()
                .orderPlace(place)
                .invoice(invoice)
                .amount(invoice.getGrandTotal()) // recommended: server source of truth
                .method(method)
                .status(PaymentStatus.PENDING)
                .gateway(req.getGateway())
                .referenceNo(referenceNo)
                .build();

        // 5) Method branching
        if (method == PaymentMethod.CASH) {
            // CASH: no omise
            // choose your behavior:
            // - If cashier confirms immediately -> PAID
            // - If cashier will confirm later -> PENDING
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(OffsetDateTime.now());

            return paymentMapper.toResponse(paymentRepo.save(payment));
        }

        // PROMPTPAY_QR: call Omise
        if (!"OMISE".equalsIgnoreCase(req.getGateway())) {
            throw new IllegalArgumentException("gateway must be OMISE for PROMPTPAY_QR");
        }

        long amountSatang = toSatang(invoice.getGrandTotal());
        var result = omiseGateway.createPromptPayCharge(amountSatang, "THB", referenceNo);

        payment.setGatewayPaymentId(result.chargeId());
        payment.setQrPayload(result.qrPayload());
        payment.setQrImageUrl(result.qrImageUrl());

        if (req.getPromptPayId() != null && !req.getPromptPayId().isBlank()) {
            payment.setPromptPayId(req.getPromptPayId());
        }

        return paymentMapper.toResponse(paymentRepo.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        Payment payment = paymentRepo.findWithDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));

        return paymentMapper.toResponse(payment);
    }

    private long toSatang(BigDecimal thb) {
        return thb.movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
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
