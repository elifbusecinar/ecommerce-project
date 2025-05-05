package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ProductRepository;
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
public class ProductService {
    private final ProductRepository productRepository;
    private static final int LOW_STOCK_THRESHOLD = 5;

    public Product createProduct(Product product) {
        product.setActive(true);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setCategory(productDetails.getCategory());
        product.setImages(productDetails.getImages());
        product.setSpecifications(productDetails.getSpecifications());
        
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    public Page<Product> searchProducts(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable);
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategory_Id(categoryId, pageable);
    }

    public List<Product> getSimilarProducts(Long productId, Long categoryId, Pageable pageable) {
        return productRepository.findSimilarProducts(categoryId, productId, pageable);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public void updateStock(Long id, int quantity) {
        Product product = getProductById(id);
        int newStock = product.getStockQuantity() + quantity;
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }
        
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts(LOW_STOCK_THRESHOLD);
    }

    public Page<Product> getTopRatedProducts(double minRating, Pageable pageable) {
        return productRepository.findTopRatedProducts(minRating, pageable);
    }

    public boolean isInStock(Long id, int requestedQuantity) {
        Product product = getProductById(id);
        return product.getStockQuantity() >= requestedQuantity;
    }
}

