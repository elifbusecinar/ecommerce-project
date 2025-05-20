package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :productId AND p.active = true")
    List<Product> findSimilarProducts(@Param("categoryId") Long categoryId, @Param("productId") Long productId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating AND p.active = true")
    Page<Product> findTopRatedProducts(@Param("minRating") double minRating, Pageable pageable);

    Page<Product> findByCategoryAndActiveTrue(Category category, Pageable pageable); // Kategoriye göre aktif ürünler

    List<Product> findByCategoryAndIdNotAndActiveTrue(Category category, Long productId, Pageable pageable); // Benzer aktif ürünler
}

