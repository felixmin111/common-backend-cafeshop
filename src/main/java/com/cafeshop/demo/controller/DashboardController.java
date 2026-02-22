package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.DashboardResponseDto;
import com.cafeshop.demo.dto.RevenuePointDto;
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
            @RequestParam RevenueFilterType type
    ) {
        return ResponseEntity.ok(dashboardService.getRevenueChart(type));
    }
}
