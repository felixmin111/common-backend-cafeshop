package com.cafeshop.demo.dto.category;


import java.time.Instant;

public record CategoryDto(
        Long id,
        String name,
        String slug,
        Boolean active,
        long menuItemCount,
        Instant updatedAt,
        Instant createdAt
) {}