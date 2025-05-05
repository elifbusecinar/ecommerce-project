package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductId(Long productId, Pageable pageable);
    
    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double calculateAverageRating(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating >= :minRating")
    List<Review> findHighRatedReviews(@Param("productId") Long productId, @Param("minRating") int minRating);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.rating = :rating")
    long countByProductIdAndRating(@Param("productId") Long productId, @Param("rating") int rating);
} 