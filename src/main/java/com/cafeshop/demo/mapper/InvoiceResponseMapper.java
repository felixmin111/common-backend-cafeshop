package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.ingredient.IngredientResponse;
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
    @Mapping(target = "vatRate", source = "vatRate")
    @Mapping(target = "vatName", source = "vatName")
    @Mapping(target = "vatType", source =  "vatType")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "ingredientResponses", source = "order.orderIngredients")
    InvoiceOrderLineResponse toOrderLine(InvoiceOrder invoiceOrder);

    PaymentResponse toPayment(Payment payment);

    // map each OrderIngredient to ingredient response dto
    @Mapping(target = "id", source = "id")
    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "name", source = "ingredient.name")
    @Mapping(target = "price", source = "ingredient.price")
    @Mapping(target = "qty", source = "qty")
    @Mapping(target = "note", source = "note")
    IngredientResponse toIngredientResponse(OrderIngredient orderIngredient);

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