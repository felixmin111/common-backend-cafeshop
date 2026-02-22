package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.DashboardResponseDto;
import com.cafeshop.demo.dto.RevenuePointDto;
import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import com.cafeshop.demo.mode.enums.RevenueFilterType;
import com.cafeshop.demo.repository.OrderPlaceRepository;
import com.cafeshop.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

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
        Double todayProfit = orderRepository.sumProfitBetween(todayStart, todayEnd);
        Double yesterdayProfit = orderRepository.sumProfitBetween(yesterdayStart, yesterdayEnd);

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

    public List<RevenuePointDto> getRevenueChart(RevenueFilterType type) {

        ZoneId zone = ZoneId.of("Asia/Bangkok");
        List<RevenuePointDto> result = new ArrayList<>();

        ZonedDateTime now = ZonedDateTime.now(zone);

        switch (type) {

            case DAILY -> {
                // last 7 days
                for (int i = 6; i >= 0; i--) {
                    ZonedDateTime start = now.minusDays(i).toLocalDate().atStartOfDay(zone);
                    ZonedDateTime end = start.plusDays(1);

                    Double revenue = orderRepository.sumProfitBetween(
                            start.toOffsetDateTime(),
                            end.toOffsetDateTime()
                    );

                    result.add(new RevenuePointDto(
                            start.getDayOfWeek().name().substring(0,3),
                            revenue
                    ));
                }
            }
            case MONTHLY -> {
                // last 12 months
                for (int i = 11; i >= 0; i--) {
                    ZonedDateTime start = now.minusMonths(i)
                            .withDayOfMonth(1)
                            .toLocalDate()
                            .atStartOfDay(zone);

                    ZonedDateTime end = start.plusMonths(1);

                    Double revenue = orderRepository.sumProfitBetween(
                            start.toOffsetDateTime(),
                            end.toOffsetDateTime()
                    );

                    result.add(new RevenuePointDto(
                            start.getMonth().name().substring(0,3),
                            revenue
                    ));
                }
            }
        }

        return result;
    }
}
