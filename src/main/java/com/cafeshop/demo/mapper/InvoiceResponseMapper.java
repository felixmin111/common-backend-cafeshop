package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.invoice.*;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mode.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceResponseMapper {

    @Mapping(target = "orderPlaceId", source = "orderPlace.id")
    @Mapping(target = "no", source = "orderPlace.no")
    @Mapping(target = "type", source = "orderPlace.type")
    @Mapping(target = "orderPlaceName",
            expression = "java(buildOrderPlaceName(invoice.getOrderPlace()))")
    @Mapping(target = "orders", source = "invoiceOrders")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(target = "orderId", source = "order.id")
    InvoiceOrderLineResponse toOrderLine(InvoiceOrder invoiceOrder);

    PaymentResponse toPayment(Payment payment);

    default String buildOrderPlaceName(OrderPlace op) {
        if (op == null) return null;

        String no = op.getNo();
        String type = op.getType() == null ? null : op.getType();

        if (no == null && type == null) return null;
        if (no == null) return type;
        if (type == null) return no;

        return no + " (" + type + ")";
    }
}

