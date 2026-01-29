package com.cafeshop.demo.dto.orderPlace;

import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlaceRequest {

    @NotBlank
    private String no;

    private Integer seat;

    @NotBlank
    private String type;

    private String description;

    private OrderPlaceStatus status;
}
