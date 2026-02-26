package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.payment.PaymentCreateRequest;
import com.cafeshop.demo.dto.payment.PaymentResponse;
import com.cafeshop.demo.dto.payment.PaymentStatusUpdateRequest;
import com.cafeshop.demo.mode.Payment;
import com.cafeshop.demo.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
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

    @PatchMapping("/{paymentId}/status")
    public Payment updateStatus(
            @PathVariable Long paymentId,
            @RequestBody PaymentStatusUpdateRequest req
    ) {
        if (req.getStatus() == null || req.getStatus().name().isEmpty()) {
            throw new IllegalArgumentException("status is required");
        }
        System.out.println("Status: " + req.getStatus());
        return service.updatePaymentStatus(paymentId, req.getStatus());
    }

}
