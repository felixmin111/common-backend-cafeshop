package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.review.ReviewRequest;
import com.cafeshop.demo.dto.review.ReviewResponse;
import com.cafeshop.demo.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/menu-items/{menuItemId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public void createReview(@PathVariable Long menuItemId,
                             @Valid @RequestBody ReviewRequest request) {

        reviewService.addReview(menuItemId, request);
    }

    @GetMapping
    public List<ReviewResponse> getReviews(@PathVariable Long menuItemId) {
        return reviewService.getReviews(menuItemId);
    }
}