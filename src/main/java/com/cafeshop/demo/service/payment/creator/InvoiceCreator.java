package com.cafeshop.demo.service.payment.creator;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.mode.Order;
import com.cafeshop.demo.mode.OrderPlace;
import com.cafeshop.demo.mode.Vat;
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
    private final OrderRepository orderRepository;
    private final VatService vatService;

    public Invoice createInvoice(PaymentCreateRequest req) {

        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found"));

        // ✅ 1. Get orders by place
        List<Order> orders = orderRepository.findByOrderPlaceId(place.getId());

        Vat vat = vatService.getDefaultVat();

        if (vat == null) {
            throw new IllegalStateException("Default VAT not configured");
        }

// ✅ Normalize VAT rate
        BigDecimal vatRate = vat.getVatRate().setScale(2, RoundingMode.HALF_UP);

// ✅ Subtotal
        BigDecimal subTotal = orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

// ✅ Tax
        BigDecimal taxAmount;
        if (vat.getTaxType() == TaxType.PERCENTAGE) {
            taxAmount = subTotal
                    .multiply(vatRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            taxAmount = vatRate;
        }

// ✅ Delivery
        BigDecimal deliveryFee = BigDecimal.ZERO.setScale(2);

// ✅ Grand total
        BigDecimal grandTotal = subTotal
                .add(taxAmount)
                .add(deliveryFee)
                .setScale(2, RoundingMode.HALF_UP);

// ✅ Save
        Invoice invoice = Invoice.builder()
                .orderPlace(place)
                .customerName(req.getCustomerName())
                .status("PENDING")
                .appliedAt(OffsetDateTime.now())

                .subTotal(subTotal)
                .tax(taxAmount)
                .vatRate(vatRate)
                .vatName(vat.getVatName())

                .deliveryFee(deliveryFee)
                .grandTotal(grandTotal)
                .build();

        return invoiceRepo.save(invoice);
    }
}
//

//@Component
//@RequiredArgsConstructor
//public class InvoiceCreator {
//
//    private final InvoiceRepository invoiceRepo;
//    private final OrderPlaceRepository orderPlaceRepo;
//    private final VatService vatService;
//
//    public Invoice createInvoice(PaymentCreateRequest req) {
//
//        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
//                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found"));
//
//        Invoice invoice = Invoice.builder()
//                .orderPlace(place)
//                .customerName(req.getCustomerName())
//                .status("PENDING")
//                .appliedAt(OffsetDateTime.now())
//                .subTotal(BigDecimal.ZERO)
//                .tax(BigDecimal.ZERO)
//                .deliveryFee(BigDecimal.ZERO)
//                .grandTotal(BigDecimal.ZERO)
//                .build();
//
//        return invoiceRepo.save(invoice);
//    }
//}

