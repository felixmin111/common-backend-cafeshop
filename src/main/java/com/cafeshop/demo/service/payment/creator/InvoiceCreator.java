package com.cafeshop.demo.service.payment.creator;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.mode.Vat;
import com.cafeshop.demo.mode.enums.OrderStatus;
import com.cafeshop.demo.mode.enums.TaxType;
import com.cafeshop.demo.repository.InvoiceRepository;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import com.cafeshop.demo.repository.OrderRepository;
import com.cafeshop.demo.service.VatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InvoiceCreator {

    private final InvoiceRepository invoiceRepo;
    private final OrderPlaceRepository orderPlaceRepo;
    private final VatService vatService;

    public Invoice createInvoice(PaymentCreateRequest req) {

        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found"));

        Vat vat = vatService.getDefaultVat();

        Invoice invoice = Invoice.builder()
                .orderPlace(place)
                .customerName(req.getCustomerName())
                .status("PENDING")
                .appliedAt(OffsetDateTime.now())
                .subTotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .vatName(vat.getVatName())
                .vatRate(vat.getVatRate())
                .vatType(vat.getTaxType())
                .deliveryFee(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .build();

        return invoiceRepo.save(invoice);
    }
}