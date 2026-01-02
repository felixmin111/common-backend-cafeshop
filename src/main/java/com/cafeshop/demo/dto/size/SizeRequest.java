package com.cafeshop.demo.dto.size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SizeRequest(
        @NotBlank @Size(max = 50) String name,
        @Size(max = 30) String shortName,
        Boolean active
) {}
