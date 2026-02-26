package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.review.ReviewRequest;
import com.cafeshop.demo.dto.review.ReviewResponse;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.Review;
import com.cafeshop.demo.repository.MenuItemRepository;
import com.cafeshop.demo.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MenuItemRepository menuItemRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         MenuItemRepository menuItemRepository) {
        this.reviewRepository = reviewRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public void addReview(Long menuItemId, ReviewRequest request) {

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        Review review = new Review();
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setReviewerName(request.reviewerName());
        review.setMenuItem(menuItem);

        reviewRepository.save(review);
    }

    public List<ReviewResponse> getReviews(Long menuItemId) {

        return reviewRepository
                .findByMenuItemIdOrderByCreatedAtDesc(menuItemId)
                .stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getRating(),
                        r.getComment(),
                        r.getReviewerName(),
                        r.getCreatedAt()
                ))
                .toList();
    }

}