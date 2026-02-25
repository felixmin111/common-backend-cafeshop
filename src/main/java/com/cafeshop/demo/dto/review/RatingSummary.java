package com.cafeshop.demo.dto.review;

public record RatingSummary(
        Long menuItemId,
        Double averageRating,
        Long reviewCount
) {}