package com.cafeshop.demo.mapper;


import com.cafeshop.demo.dto.payment.PaymentIngredientResponse;
import com.cafeshop.demo.dto.payment.PaymentItemResponse;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.mode.InvoiceOrder;
import com.cafeshop.demo.mode.OrderIngredient;
import com.cafeshop.demo.mode.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "orderPlaceId", source = "orderPlace.id")
    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "invoiceNo", source = "invoice.invoiceNo")
    @Mapping(target = "customerName", source = "invoice.customerName")
    @Mapping(target = "invoiceStatus", source = "invoice.status")
    @Mapping(target = "subTotal", source = "invoice.subTotal")
    @Mapping(target = "tax", source = "invoice.tax")
    @Mapping(target = "deliveryFee", source = "invoice.deliveryFee")
    @Mapping(target = "grandTotal", source = "invoice.grandTotal")
    @Mapping(target = "appliedAt", source = "invoice.appliedAt")
    @Mapping(target = "items", source = "invoice.invoiceOrders")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "menuItemSizeId", source = "order.menuItemSize.id")
    @Mapping(target = "menuItemName", source = "menuItemName")
    @Mapping(target = "sizeName", source = "sizeName")
    @Mapping(target = "qty", source = "qty")
    @Mapping(target = "unitPrice", source = "unitPrice")
    @Mapping(target = "lineTotal", source = "lineTotal")
    @Mapping(target = "note", source = "note")
    @Mapping(
            target = "ingredients",
            source = "order.orderIngredients"
    )
    PaymentItemResponse toItem(InvoiceOrder io);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "name", source = "ingredient.name")
    @Mapping(target = "qty", source = "qty")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "price", source = "ingredient.price")
    PaymentIngredientResponse toIngredient(OrderIngredient oi);
}


