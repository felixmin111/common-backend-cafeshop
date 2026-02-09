package com.cafeshop.demo.service.payment.creator;

import com.cafeshop.demo.dto.order.OrderRequest;
import com.cafeshop.demo.dto.orderIngredient.OrderIngredientRequest;
import com.cafeshop.demo.mode.*;
import com.cafeshop.demo.mode.enums.OrderStatus;
import com.cafeshop.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCreator {

    private final OrderRepository orderRepo;
    private final InvoiceOrderRepository invoiceOrderRepo;
    private final IngredientRepository ingredientRepo;
    private final OrderIngredientRepository orderIngredientRepo;
    private final MenuItemSizeRepository menuItemSizeRepo;

    public void createOrders(Invoice invoice, List<OrderRequest> items) {

        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderRequest itemReq : items) {

            MenuItemSize mis = menuItemSizeRepo.findById(itemReq.getMenuItemSizeId())
                    .orElseThrow(() -> new IllegalArgumentException("MenuItemSize not found"));

            Order order = createOrder(invoice.getOrderPlace(), mis, itemReq);
            createIngredients(order, itemReq);
            createInvoiceOrder(invoice, order, mis, itemReq);

            subTotal = subTotal.add(order.getTotalPrice());
        }

        invoice.setSubTotal(subTotal);
        invoice.setGrandTotal(subTotal
                .add(invoice.getTax())
                .add(invoice.getDeliveryFee()));
    }

    private Order createOrder(OrderPlace place, MenuItemSize mis, OrderRequest req) {
        Order order = Order.builder()
                .orderPlace(place)
                .menuItemSize(mis)
                .qty(req.getQty())
                .unitPrice(BigDecimal.valueOf(mis.getSellPrice()))
                .totalPrice(BigDecimal.valueOf(mis.getSellPrice())
                        .multiply(BigDecimal.valueOf(req.getQty())))
                .status(OrderStatus.PENDING)
                .note(req.getNote())
                .build();

        return orderRepo.save(order);
    }

    private void createIngredients(Order order, OrderRequest req) {
        if (req.getIngredients() == null) return;

        for (OrderIngredientRequest ingReq : req.getIngredients()) {
            OrderIngredient oi = OrderIngredient.builder()
                    .order(order)
                    .ingredient(
                            ingredientRepo.findById(ingReq.getIngredientId())
                                    .orElseThrow()
                    )
                    .qty(ingReq.getQty())
                    .note(ingReq.getNote())
                    .build();

            orderIngredientRepo.save(oi);
        }
    }

    private void createInvoiceOrder(
            Invoice invoice, Order order, MenuItemSize mis, OrderRequest req) {

        InvoiceOrder io = InvoiceOrder.builder()
                .invoice(invoice)
                .order(order)
                .menuItemName(mis.getMenuItem().getName())
                .sizeName(mis.getSize().getName())
                .qty(req.getQty())
                .unitPrice(order.getUnitPrice())
                .lineTotal(order.getTotalPrice())
                .note(req.getNote())
                .build();

        invoiceOrderRepo.save(io);
    }
}
