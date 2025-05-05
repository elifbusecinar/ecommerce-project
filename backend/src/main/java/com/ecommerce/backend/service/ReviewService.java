package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Review;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ReviewRepository;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    public Review createReview(Review review) {
        if (reviewRepository.existsByUserIdAndProductId(review.getUser().getId(), review.getProduct().getId())) {
            throw new IllegalStateException("User has already reviewed this product");
        }

        Review savedReview = reviewRepository.save(review);
        updateProductRating(review.getProduct().getId());
        return savedReview;
    }

    public Review updateReview(Long id, Review reviewDetails) {
        Review review = getReviewById(id);
        
        review.setRating(reviewDetails.getRating());
        review.setComment(reviewDetails.getComment());
        
        Review updatedReview = reviewRepository.save(review);
        updateProductRating(review.getProduct().getId());
        return updatedReview;
    }

    public void deleteReview(Long id) {
        Review review = getReviewById(id);
        Long productId = review.getProduct().getId();
        
        reviewRepository.delete(review);
        updateProductRating(productId);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    public Page<Review> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    public Page<Review> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
    }

    public List<Review> getHighRatedReviews(Long productId, int minRating) {
        return reviewRepository.findHighRatedReviews(productId, minRating);
    }

    private void updateProductRating(Long productId) {
        Double averageRating = reviewRepository.calculateAverageRating(productId);
        Product product = productService.getProductById(productId);
        product.setRating(averageRating != null ? averageRating : 0.0);
        productService.updateProduct(productId, product);
    }

    public long getReviewCountByRating(Long productId, int rating) {
        return reviewRepository.countByProductIdAndRating(productId, rating);
    }
} 