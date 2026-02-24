package com.cafeshop.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RevenuePointDto {
    private LocalDate date;
    private BigDecimal revenue;
}
