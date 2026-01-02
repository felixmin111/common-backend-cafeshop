package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.size.SizeRequest;
import com.cafeshop.demo.dto.size.SizeResponse;
import com.cafeshop.demo.service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sizes")
@RequiredArgsConstructor
public class SizeAdminController {

    private final SizeService service;

    @PostMapping
    public SizeResponse create(@Valid @RequestBody SizeRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<SizeResponse> findAll(@RequestParam(required = false) Boolean active) {
        return service.findAll(active);
    }

    @GetMapping("/{id}")
    public SizeResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public SizeResponse update(@PathVariable Long id, @Valid @RequestBody SizeRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
