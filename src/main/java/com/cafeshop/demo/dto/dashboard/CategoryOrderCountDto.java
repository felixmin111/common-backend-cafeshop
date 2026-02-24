package com.cafeshop.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryOrderCountDto {
    private String categoryName;
    private Long orderCount;
}