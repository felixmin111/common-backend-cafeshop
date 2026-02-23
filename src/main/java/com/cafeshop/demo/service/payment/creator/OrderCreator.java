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

            BigDecimal baseUnitPrice = BigDecimal.valueOf(mis.getSellPrice()); // check sellPrice type!
            BigDecimal ingUnitTotal = calcIngredientUnitTotal(itemReq);        // ✅ new
            BigDecimal unitTotal = baseUnitPrice.add(ingUnitTotal);            // ✅ base + ingredients
            BigDecimal lineTotal = unitTotal.multiply(BigDecimal.valueOf(itemReq.getQty())); // ✅ * qty

            Order order = Order.builder()
                    .orderPlace(invoice.getOrderPlace())
                    .menuItemSize(mis)
                    .qty(itemReq.getQty())
                    .unitPrice(unitTotal)         // ✅ include ingredients in unit price (optional)
                    .totalPrice(lineTotal)        // ✅ correct total
                    .status(OrderStatus.PENDING)
                    .note(itemReq.getNote())
                    .build();

            order = orderRepo.save(order);

            createIngredients(order, itemReq);     // keep this
            createInvoiceOrder(invoice, order, mis, itemReq);

            subTotal = subTotal.add(order.getTotalPrice());
        }

        invoice.setSubTotal(subTotal);
        invoice.setGrandTotal(subTotal
                .add(nvl(invoice.getTax()))
                .add(nvl(invoice.getDeliveryFee())));
    }
    private BigDecimal calcIngredientUnitTotal(OrderRequest req) {
        if (req.getIngredients() == null || req.getIngredients().isEmpty()) return BigDecimal.ZERO;

        BigDecimal sum = BigDecimal.ZERO;
        for (com.cafeshop.demo.dto.orderIngredient.OrderIngredientRequest ingReq : req.getIngredients()) {
            Ingredient ing = ingredientRepo.findById(ingReq.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException("Ingredient not found: " + ingReq.getIngredientId()));

            BigDecimal ingPrice = nvl(ing.getPrice()); // ingredient price in DB
            BigDecimal ingQty = nvl(ingReq.getQty());  // qty from request
            sum = sum.add(ingPrice.multiply(ingQty));
        }
        return sum;
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
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
