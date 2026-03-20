package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.dashboard.CategoryOrderCountDto;
import com.cafeshop.demo.dto.dashboard.DashboardResponseDto;
import com.cafeshop.demo.dto.dashboard.RevenuePointDto;
import com.cafeshop.demo.dto.dashboard.TopItemDto;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.mode.enums.RevenueFilterType;
import com.cafeshop.demo.repository.InvoiceRepository;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import com.cafeshop.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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
    private final InvoiceRepository invoiceRepository;

    public DashboardResponseDto getDashboard(String type, String period) {

        OffsetDateTime todayStart;
        OffsetDateTime todayEnd;
        OffsetDateTime yesterdayStart;
        OffsetDateTime yesterdayEnd;

        if (type.equals("DAILY")) {
            LocalDate date = LocalDate.parse(period);

            todayStart = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            todayEnd = todayStart.plusDays(1);

            yesterdayStart = todayStart.minusDays(1);
            yesterdayEnd = todayStart;

        } else { // MONTHLY
            YearMonth month = YearMonth.parse(period);

            LocalDate firstDay = month.atDay(1);
            LocalDate nextMonth = month.plusMonths(1).atDay(1);

            ZoneOffset offset = ZoneOffset.ofHours(7);

            todayStart = firstDay.atStartOfDay().atOffset(offset);
            todayEnd = nextMonth.atStartOfDay().atOffset(offset);

// previous month
            LocalDate prevMonth = month.minusMonths(1).atDay(1);

            yesterdayStart = prevMonth.atStartOfDay().atOffset(offset);
            yesterdayEnd = firstDay.atStartOfDay().atOffset(offset);
        }

        // Orders
//        Long todayOrders = orderRepository.countOrdersBetween(todayStart, todayEnd);
//        Long yesterdayOrders = orderRepository.countOrdersBetween(yesterdayStart, yesterdayEnd);
        Long todayOrders = invoiceRepository.countCompletedInvoicesBetween(todayStart, todayEnd);
        Long yesterdayOrders = invoiceRepository.countCompletedInvoicesBetween(yesterdayStart, yesterdayEnd);

        Double orderGrowth = calculateGrowth(todayOrders, yesterdayOrders);

        // Profit
        BigDecimal todayProfit = orderRepository.sumProfitBetween(todayStart, todayEnd);
        BigDecimal yesterdayProfit = orderRepository.sumProfitBetween(yesterdayStart, yesterdayEnd);

        Double profitGrowth = calculateGrowth(todayProfit, yesterdayProfit);

        // Tables (unchanged)
        Long activeTables = orderPlaceRepository.countByStatus(OrderPlaceStatus.ACTIVE);
        Long totalTables = orderPlaceRepository.countAllActiveTables();

        // Popular Item (based on selected period!)
        List<Object[]> popularItems = orderRepository.findPopularItems(todayStart, todayEnd);

        // Sales (rename from profit)
        BigDecimal todaySales = orderRepository.sumTotalSalesBetween(todayStart, todayEnd);
        BigDecimal yesterdaySales = orderRepository.sumTotalSalesBetween(yesterdayStart, yesterdayEnd);

        Double salesGrowth = calculateGrowth(todaySales, yesterdaySales);

        String popularName = null;
        Long popularCount = 0L;

        if (!popularItems.isEmpty()) {
            popularName = (String) popularItems.get(0)[0];
            popularCount = (Long) popularItems.get(0)[1];
        }

        return DashboardResponseDto.builder()
                .todayOrders(todayOrders)
                .yesterdayOrders(yesterdayOrders)
                .orderGrowthPercent(orderGrowth)
                .todayProfitBaht(todayProfit)
                .profitGrowthPercent(profitGrowth)
                .totalSales(todaySales)
                .previousSales(yesterdaySales)
                .salesGrowthPercent(salesGrowth)
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
                        orderRepository.sumSalesBetween(start, end);

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

                    BigDecimal revenue = orderRepository.sumTotalSalesBetween(
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

    public List<RevenuePointDto> getProfitChart(
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
                        orderRepository.sumProfitBetweenChart(start, end);

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

                    BigDecimal revenue = orderRepository.sumProfitBetween(
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

    public List<TopItemDto> getTopItems(
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

        List<Object[]> rows = orderRepository.findTopSellingItems(start, end);

        List<TopItemDto> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(new TopItemDto(
                    (String) row[0],
                    (Long) row[1]
            ));
        }

        // ✅ LIMIT TOP 5
        return result.stream().limit(5).toList();
    }
}
