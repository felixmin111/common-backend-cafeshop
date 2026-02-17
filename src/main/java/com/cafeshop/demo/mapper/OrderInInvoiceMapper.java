package com.cafeshop.demo.mapper;

import com.cafeshop.demo.dto.invoice.InvoiceOrderLineResponse;
import com.cafeshop.demo.dto.invoice.InvoiceResponse;
import com.cafeshop.demo.dto.orderIngredient.OrderIngredientResponse;
import com.cafeshop.demo.mode.Invoice;
import com.cafeshop.demo.mode.InvoiceOrder;
import com.cafeshop.demo.mode.Order;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { OrderIngredientResponseMapper.class }
)
public interface OrderInInvoiceMapper {

    @Mapping(target = "orderPlaceId", source = "orderPlace.id")
    @Mapping(target = "orders", source = "invoiceOrders")
    InvoiceResponse toResponse(Invoice invoice);

    @Mapping(target = "orderId", ignore = true) // we set it in service after loading orders with ingredients
    InvoiceOrderLineResponse toOrderLine(InvoiceOrder invoiceOrder);
}
