package com.cafeshop.demo.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopItemDto {
    private String name;
    private Long quantity;
}