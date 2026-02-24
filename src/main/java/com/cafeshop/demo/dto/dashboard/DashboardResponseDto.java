package com.cafeshop.demo.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponseDto {

    private Long todayOrders;
    private Long yesterdayOrders;
    private Double orderGrowthPercent;

    private BigDecimal todayProfitBaht;
    private Double profitGrowthPercent;

    private Long activeTables;
    private Long totalTables;

    private String popularItemName;
    private Long popularItemCount;
}