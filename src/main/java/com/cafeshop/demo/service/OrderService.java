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

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final MenuItemSizeRepository menuItemSizeRepo;
    private final OrderPlaceRepository orderPlaceRepo;
    private final IngredientRepository ingredientRepo;
    private final OrderMapper mapper;

    public OrderResponse create(OrderRequest req) {
        System.out.println("Order Ingredients Request: " + req.getIngredients().size());
        System.out.println("Order Ingredients Request: " + req.getIngredients().stream().toList().getFirst().toString());

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
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order entity = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepo.findAllWithDetails().stream().map(mapper::toResponse).toList();
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
