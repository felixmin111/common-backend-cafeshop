package com.cafeshop.demo.controller;

// package com.cafeshop.demo.controller.admin;

import com.cafeshop.demo.dto.invoice.InvoiceResponse;
import com.cafeshop.demo.service.InvoiceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/invoices")
public class InvoiceController {

    private final InvoiceQueryService service;

    @GetMapping
    public List<InvoiceResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
}

