package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.orderPlace.OrderPlaceRequest;
import com.cafeshop.demo.dto.orderPlace.OrderPlaceResponse;
import com.cafeshop.demo.service.OrderPlaceQueryService;
import com.cafeshop.demo.service.OrderPlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order-places")
@RequiredArgsConstructor
public class OrderPlaceController {
    private final OrderPlaceQueryService orderPlaceQueryService;
    private final OrderPlaceService service;

    @PostMapping
    public ResponseEntity<OrderPlaceResponse> create(@Valid @RequestBody OrderPlaceRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderPlaceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderPlaceResponse>> getAll() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderPlaceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderPlaceRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/with-current-order")
    public ResponseEntity<List<OrderPlaceResponse>> getAllWithCurrentOrder() {
        return ResponseEntity.ok(orderPlaceQueryService.getAllWithCurrentOrder());
    }
}

