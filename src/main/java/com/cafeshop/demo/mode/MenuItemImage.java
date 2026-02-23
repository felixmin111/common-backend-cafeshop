package com.cafeshop.demo.mode;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_item_images",
        indexes = {
                @Index(name = "idx_menu_item_images_menu_item_id", columnList = "menu_item_id")
        })
public class MenuItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many images belong to one menu item
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    // S3 object key (ex: products/12/uuid.jpg)
    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    // Public URL (or store only key and generate URL later)
    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    // optional: mark one as main image
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean primary = false;

    // soft delete (matches your "active" pattern)
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
        if (primary == null) primary = false;
        if (active == null) active = true;
    }
}