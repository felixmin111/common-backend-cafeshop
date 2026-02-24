package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.dashboard.CategoryOrderCountDto;
import com.cafeshop.demo.dto.dashboard.DashboardResponseDto;
import com.cafeshop.demo.dto.dashboard.RevenuePointDto;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.mode.enums.RevenueFilterType;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import com.cafeshop.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderPlaceRepository orderPlaceRepository;

    public DashboardResponseDto getDashboard() {

        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime todayStart = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime todayEnd = todayStart.plusDays(1);

        OffsetDateTime yesterdayStart = todayStart.minusDays(1);
        OffsetDateTime yesterdayEnd = todayStart;

        // Orders
        Long todayOrders = orderRepository.countOrdersBetween(todayStart, todayEnd);
        Long yesterdayOrders = orderRepository.countOrdersBetween(yesterdayStart, yesterdayEnd);

        Double orderGrowth = calculateGrowth(todayOrders, yesterdayOrders);

        // PROFIT (Baht difference)
        BigDecimal todayProfit = orderRepository.sumRevenueBetween(todayStart, todayEnd);
        BigDecimal yesterdayProfit = orderRepository.sumRevenueBetween(yesterdayStart, yesterdayEnd);

        Double profitGrowth = calculateGrowth(todayProfit, yesterdayProfit);

        // Tables
        Long activeTables = orderPlaceRepository.countByStatus(OrderPlaceStatus.ACTIVE);
        Long totalTables = orderPlaceRepository.countAllActiveTables();

        // Popular Item
        List<Object[]> popularItems = orderRepository.findPopularItems(todayStart, todayEnd);

        String popularName = null;
        Long popularCount = 0L;

        if (!popularItems.isEmpty()) {
            popularName = (String) popularItems.getFirst()[0];
            popularCount = (Long) popularItems.getFirst()[1];
        }

        return DashboardResponseDto.builder()
                .todayOrders(todayOrders)
                .yesterdayOrders(yesterdayOrders)
                .orderGrowthPercent(orderGrowth)
                .todayProfitBaht(todayProfit)
                .profitGrowthPercent(profitGrowth)
                .activeTables(activeTables)
                .totalTables(totalTables)
                .popularItemName(popularName)
                .popularItemCount(popularCount)
                .build();
    }

    private Double calculateGrowth(Number today, Number yesterday) {
        double y = yesterday.doubleValue();
        if (y == 0) return 100.0;
        return ((today.doubleValue() - y) / y) * 100;
    }

    public List<RevenuePointDto> getRevenueChart(
            RevenueFilterType type,
            String period
    ) {

        ZoneId zone = ZoneId.of("Asia/Bangkok");

        switch (type) {

            case DAILY -> {

                LocalDate selected =
                        period != null
                                ? LocalDate.parse(period)
                                : LocalDate.now(zone);

                LocalDate startDate = selected.minusDays(6);
                LocalDate endDate = selected.plusDays(1);

                OffsetDateTime start = startDate.atStartOfDay(zone).toOffsetDateTime();
                OffsetDateTime end = endDate.atStartOfDay(zone).toOffsetDateTime();

                List<Object[]> rows =
                        orderRepository.sumProfitBetween(start, end);

                Map<LocalDate, BigDecimal> revenueMap = new HashMap<>();

                for (Object[] row : rows) {
                    revenueMap.put(
                            (LocalDate) row[0],      // ✅ FIXED HERE
                            (BigDecimal) row[1]
                    );
                }

                List<RevenuePointDto> result = new ArrayList<>();

                for (int i = 0; i < 7; i++) {
                    LocalDate date = startDate.plusDays(i);

                    BigDecimal revenue =
                            revenueMap.getOrDefault(date, BigDecimal.ZERO);

                    result.add(new RevenuePointDto(date, revenue));
                }

                return result;
            }
            case MONTHLY -> {

                List<RevenuePointDto> result = new ArrayList<>();
                ZonedDateTime now = ZonedDateTime.now(zone);

                for (int i = 11; i >= 0; i--) {

                    ZonedDateTime start = now.minusMonths(i)
                            .withDayOfMonth(1)
                            .toLocalDate()
                            .atStartOfDay(zone);

                    ZonedDateTime end = start.plusMonths(1);

                    BigDecimal revenue = orderRepository.sumRevenueBetween(
                            start.toOffsetDateTime(),
                            end.toOffsetDateTime()
                    );

                    result.add(new RevenuePointDto(
                            start.toLocalDate(),
                            revenue
                    ));
                }

                return result;
            }
        }

        return List.of();
    }
    public List<CategoryOrderCountDto> getCategoryOrderCounts(
            RevenueFilterType type,
            String period
    ) {

        OffsetDateTime start;
        OffsetDateTime end;

        if (type == RevenueFilterType.DAILY) {
            LocalDate date = LocalDate.parse(period);
            start = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            end = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        } else {
            YearMonth ym = YearMonth.parse(period);
            start = ym.atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            end = ym.plusMonths(1).atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        }

        return orderRepository.countOrdersByCategoryBetween(start, end);
    }
}
