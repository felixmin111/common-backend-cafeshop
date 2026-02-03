package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.dto.payment.PaymentStatusUpdateRequest;
import com.cafeshop.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

//    @GetMapping("/by-order-place/{orderPlaceId}")
//    public ResponseEntity<List<PaymentResponse>> byOrderPlace(@PathVariable Long orderPlaceId) {
//        return ResponseEntity.ok(service.getByOrderPlace(orderPlaceId));
//    }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<PaymentResponse> updateStatus(
//            @PathVariable Long id,
//            @Valid @RequestBody PaymentStatusUpdateRequest req
//    ) {
//        return ResponseEntity.ok(service.updateStatus(id, req));
//    }

}
