package com.cafeshop.demo.repository;

import com.cafeshop.demo.dto.review.RatingSummary;
import com.cafeshop.demo.mode.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMenuItemIdOrderByCreatedAtDesc(Long menuItemId);

    long countByMenuItemId(Long menuItemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.menuItem.id = :menuItemId")
    Double findAverageRatingByMenuItemId(Long menuItemId);

    @Query("""
SELECT new com.cafeshop.demo.dto.review.RatingSummary(
    r.menuItem.id,
    COALESCE(AVG(r.rating), 0.0),
    COUNT(r)
)
FROM Review r
WHERE r.menuItem.id = :menuItemId
GROUP BY r.menuItem.id
""")
    Optional<RatingSummary> getRatingSummary(Long menuItemId);

    @Query("""
SELECT new com.cafeshop.demo.dto.review.RatingSummary(
    r.menuItem.id,
    COALESCE(AVG(r.rating), 0.0),
    COUNT(r)
)
FROM Review r
GROUP BY r.menuItem.id
""")
    List<RatingSummary> getRatingSummariesGrouped();
}
