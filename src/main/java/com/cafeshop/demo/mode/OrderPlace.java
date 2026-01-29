package com.cafeshop.demo.mode;

import com.cafeshop.demo.mode.enums.OrderPlaceStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "no", length = 40, nullable = false)
    private String no;

    @Column(name = "seat")
    private Integer seat;

    @Column(name = "type", length = 40, nullable = false)
    private String type;

    @Column(name = "description", length = 300)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private OrderPlaceStatus status;
}
