package com.cafeshop.demo.dto.size;

public record SizeResponse(
        Long id,
        String name,
        String shortName,
        Boolean active
) {}
