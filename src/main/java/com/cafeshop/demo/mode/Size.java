package com.cafeshop.demo.mode;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sizes")
@Getter @Setter
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "short_name", length = 30)
    private String shortName;

    @Column(nullable = false)
    private Boolean active = true; // ✅ optional but recommended
}
