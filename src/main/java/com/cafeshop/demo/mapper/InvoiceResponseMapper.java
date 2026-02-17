package com.cafeshop.demo.mapper;

// package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.invoice.*;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mode.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceResponseMapper {

    @Mapping(target = "orderPlaceId", source = "orderPlace.id")

    @Mapping(target = "orders", source = "invoiceOrders")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(target = "orderId", source = "order.id")
    InvoiceOrderLineResponse toOrderLine(InvoiceOrder invoiceOrder);

    PaymentResponse toPayment(Payment payment);
}

