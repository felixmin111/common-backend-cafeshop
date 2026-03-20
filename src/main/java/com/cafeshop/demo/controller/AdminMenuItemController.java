package com.cafeshop.demo.controller;
import com.cafeshop.demo.dto.menuItem.MenuItemCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.dto.menuItem.MenuItemStatusUpdateRequest;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import com.cafeshop.demo.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/menu-items")
@RequiredArgsConstructor
public class AdminMenuItemController {

    private final MenuItemService service;

    @PostMapping
    public MenuItemResponse create(@Valid @RequestBody MenuItemCreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<MenuItemResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MenuItemResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/status/{status}")
    public List<MenuItemResponse> findByStatus(@PathVariable MenuItemStatus status) {
        return service.findByStatus(status);
    }

    @PutMapping("/{id}/status")
    public MenuItemResponse updateStatus(
            @PathVariable Long id,
            @RequestBody MenuItemStatusUpdateRequest request
    ) {
        return service.updateStatus(id, request.getStatus());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public MenuItemResponse update(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemCreateRequest request
    ) {
        return service.update(id, request);
    }
}

