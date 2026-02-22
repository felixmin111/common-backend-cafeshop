package com.cafeshop.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenuePointDto {
    private String label;   // e.g. "Mon", "Week 1", "Jan"
    private Double revenue;
}