package com.cafeshop.demo.service.payment.creator;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.repository.InvoiceRepository;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class InvoiceCreator {

    private final InvoiceRepository invoiceRepo;
    private final OrderPlaceRepository orderPlaceRepo;

    public Invoice createInvoice(PaymentCreateRequest req) {

        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found"));

        Invoice invoice = Invoice.builder()
                .orderPlace(place)
                .customerName(req.getCustomerName())
                .status("PENDING")
                .appliedAt(OffsetDateTime.now())
                .subTotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .build();

        return invoiceRepo.save(invoice);
    }
}

