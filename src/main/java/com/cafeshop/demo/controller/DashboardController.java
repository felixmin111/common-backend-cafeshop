package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.dashboard.CategoryOrderCountDto;
import com.cafeshop.demo.dto.dashboard.DashboardResponseDto;
import com.cafeshop.demo.dto.dashboard.RevenuePointDto;
import com.cafeshop.demo.mode.enums.RevenueFilterType;
import com.cafeshop.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<RevenuePointDto>> getRevenueChart(
            @RequestParam RevenueFilterType type,
            @RequestParam(required = false) String period
    ){
        return ResponseEntity.ok(dashboardService.getRevenueChart(type, period));
    }

    @GetMapping("/category-orders")
    public ResponseEntity<List<CategoryOrderCountDto>> getCategoryOrders(
            @RequestParam RevenueFilterType type,
            @RequestParam(required = false) String period
    ) {
        return ResponseEntity.ok(
                dashboardService.getCategoryOrderCounts(type, period)
        );
    }
}
