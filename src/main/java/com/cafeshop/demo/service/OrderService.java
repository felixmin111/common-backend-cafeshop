package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.order.OrderRequest;
import com.cafeshop.demo.dto.order.OrderResponse;
import com.cafeshop.demo.dto.orderIngredient.OrderIngredientRequest;
import com.cafeshop.demo.mapper.OrderMapper;
import com.cafeshop.demo.mode.*;
import com.cafeshop.demo.mode.enums.OrderStatus;
import com.cafeshop.demo.repository.*;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final MenuItemSizeRepository menuItemSizeRepo;
    private final OrderPlaceRepository orderPlaceRepo;
    private final IngredientRepository ingredientRepo;
    private final InvoiceRepository invoiceRepo;
    private final OrderMapper mapper;
    private final InvoiceOrderRepository invoiceOrderRepo;

    public OrderResponse create(OrderRequest req) {
        System.out.println("Order Ingredients Request: " + req.getIngredients().size());

        MenuItemSize size = menuItemSizeRepo.findById(req.getMenuItemSizeId())
                .orElseThrow(() -> new IllegalArgumentException("MenuItemSize not found: " + req.getMenuItemSizeId()));

        OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + req.getOrderPlaceId()));

        BigDecimal unitPrice = BigDecimal.valueOf(size.getSellPrice());
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(req.getQty()));

        Order entity = Order.builder()
                .customerName(req.getCustomerName())
                .qty(req.getQty())
                .note(req.getNote())
                .menuItemSize(size)
                .orderPlace(place)
                .status(req.getStatus() == null ? OrderStatus.PENDING : req.getStatus())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();

        if (req.getIngredients() != null && !req.getIngredients().isEmpty()) {
            for (OrderIngredientRequest ingReq : req.getIngredients()) {

                Ingredient ingredient = ingredientRepo.findById(ingReq.getIngredientId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Ingredient not found: " + ingReq.getIngredientId()));
                System.out.println("Ingredient Ingredients Request: " + ingredient.toString());

                OrderIngredient oi = OrderIngredient.builder()
                        .ingredient(ingredient)
                        .qty(ingReq.getQty())
                        .note(ingReq.getNote())
                        .build();

                entity.addIngredient(oi);
            }
        }

        Order saved = orderRepo.save(entity);
        System.out.println("Order created: " + saved.toString());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {

        // 1) fetch order with details
        Order entity = orderRepo.findOrderDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        // 2) map to DTO first
        OrderResponse dto = mapper.toResponse(entity);

        // 3) map orderId -> invoiceId (single order)
        Long invoiceId = invoiceOrderRepo.findInvoiceIdsByOrderIds(List.of(id)).stream()
                .map(r -> (Long) r[1])     // r[0]=orderId, r[1]=invoiceId
                .findFirst()
                .orElse(null);

        // 4) fetch invoice payment status (single invoice)
        String payStatus = "__";
        if (invoiceId != null) {
            payStatus = invoiceRepo.findPaymentStatusByInvoiceIds(List.of(invoiceId)).stream()
                    .map(r -> (String) r[1]) // r[0]=invoiceId, r[1]=paymentStatus
                    .findFirst()
                    .orElse("__");
        }

        // 5) set extra fields
        dto.setInvoiceId(invoiceId);
        dto.setInvoicePaymentStatus(payStatus);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        var orders = orderRepo.findAllWithDetails();

        // 1) map to DTO first
        var dtos = orders.stream().map(mapper::toResponse).toList();

        // 2) get orderIds
        var orderIds = orders.stream().map(Order::getId).toList();

        // 3) map orderId -> invoiceId
        Map<Long, Long> orderIdToInvoiceId = invoiceOrderRepo.findInvoiceIdsByOrderIds(orderIds).stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1],
                        (a, b) -> a
                ));

        // 4) fetch invoice payment statuses
        var invoiceIds = orderIdToInvoiceId.values().stream().distinct().toList();

        Map<Long, String> invoiceIdToPayStatus = invoiceRepo.findPaymentStatusByInvoiceIds(invoiceIds).stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (String) r[1],
                        (a, b) -> a
                ));

        for (var dto : dtos) {
            Long invoiceId = orderIdToInvoiceId.get(dto.getId());
            String payStatus = (invoiceId == null) ? "__" : invoiceIdToPayStatus.getOrDefault(invoiceId, "__");
            dto.setInvoicePaymentStatus(payStatus);
            dto.setInvoiceId(invoiceId);
        }

        return dtos;
    }

    public OrderResponse update(Long id, OrderRequest req) {
        Order entity = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        if (req.getMenuItemSizeId() != null) {
            MenuItemSize size = menuItemSizeRepo.findById(req.getMenuItemSizeId())
                    .orElseThrow(() -> new IllegalArgumentException("MenuItemSize not found: " + req.getMenuItemSizeId()));
            entity.setMenuItemSize(size);
            entity.setUnitPrice(BigDecimal.valueOf(size.getSellPrice()));
        }

        if (req.getOrderPlaceId() != null) {
            OrderPlace place = orderPlaceRepo.findById(req.getOrderPlaceId())
                    .orElseThrow(() -> new IllegalArgumentException("OrderPlace not found: " + req.getOrderPlaceId()));
            entity.setOrderPlace(place);
        }

        if (req.getCustomerName() != null) entity.setCustomerName(req.getCustomerName());
        if (req.getQty() != null) entity.setQty(req.getQty());
        if (req.getNote() != null) entity.setNote(req.getNote());
        if (req.getStatus() != null) entity.setStatus(req.getStatus());

        entity.setTotalPrice(entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQty())));

        return mapper.toResponse(entity);
    }

    public void delete(Long id) {
        orderRepo.deleteById(id);
    }
}
