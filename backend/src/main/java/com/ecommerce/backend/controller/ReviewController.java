package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Review;
import com.ecommerce.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Review", description = "Product review management APIs")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(
        summary = "Create new review",
        description = "Creates a new product review (Authenticated users only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review created successfully", 
            content = @Content(schema = @Schema(implementation = Review.class))),
        @ApiResponse(responseCode = "400", description = "Invalid review data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Review> createReview(
        @Parameter(description = "Review details to create", required = true)
        @RequestBody Review review
    ) {
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    @Operation(
        summary = "Update review",
        description = "Updates an existing review (Authenticated users only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review updated successfully", 
            content = @Content(schema = @Schema(implementation = Review.class))),
        @ApiResponse(responseCode = "400", description = "Invalid review data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this review"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Review> updateReview(
        @Parameter(description = "ID of the review to update") @PathVariable Long id,
        @Parameter(description = "Updated review details") @RequestBody Review reviewDetails
    ) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewDetails));
    }

    @Operation(
        summary = "Delete review",
        description = "Deletes a review (Authenticated users only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this review"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(
        @Parameter(description = "ID of the review to delete") @PathVariable Long id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get review by ID",
        description = "Retrieves a specific review by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved review", 
            content = @Content(schema = @Schema(implementation = Review.class))),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(
        @Parameter(description = "ID of the review to retrieve") @PathVariable Long id
    ) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @Operation(
        summary = "Get product reviews",
        description = "Retrieves all reviews for a specific product with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product reviews"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<Review>> getProductReviews(
        @Parameter(description = "ID of the product") @PathVariable Long productId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @Operation(
        summary = "Get user reviews",
        description = "Retrieves all reviews by a specific user with pagination (Authenticated users only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user reviews"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view these reviews"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Review>> getUserReviews(
        @Parameter(description = "ID of the user") @PathVariable Long userId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId, pageable));
    }

    @Operation(
        summary = "Get high-rated reviews",
        description = "Retrieves reviews with ratings above the specified minimum for a product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved high-rated reviews"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}/high-rated")
    public ResponseEntity<List<Review>> getHighRatedReviews(
        @Parameter(description = "ID of the product") @PathVariable Long productId,
        @Parameter(description = "Minimum rating threshold (default: 4)") @RequestParam(defaultValue = "4") int minRating
    ) {
        return ResponseEntity.ok(reviewService.getHighRatedReviews(productId, minRating));
    }

    @Operation(
        summary = "Get review count by rating",
        description = "Counts the number of reviews with a specific rating for a product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully counted reviews"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}/rating-count")
    public ResponseEntity<Long> getReviewCountByRating(
        @Parameter(description = "ID of the product") @PathVariable Long productId,
        @Parameter(description = "Rating to count reviews for") @RequestParam int rating
    ) {
        return ResponseEntity.ok(reviewService.getReviewCountByRating(productId, rating));
    }
} 