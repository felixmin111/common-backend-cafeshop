package com.cafeshop.demo.mode;

import com.cafeshop.demo.mode.enums.TaxType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "vats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vat_code", nullable = false, unique = true)
    private String vatCode;

    @Column(name = "vat_name", nullable = false)
    private String vatName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false)
    private TaxType taxType;

    @Column(name = "vat_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal vatRate;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
