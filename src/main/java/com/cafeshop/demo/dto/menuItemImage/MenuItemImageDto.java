package com.cafeshop.demo.dto.menuItemImage;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemImageDto {
    private Long id;
    private String url;
    private Boolean primary;
    private String contentType;
    private Long sizeBytes;
    private Instant createdAt;
    private Boolean active;
}