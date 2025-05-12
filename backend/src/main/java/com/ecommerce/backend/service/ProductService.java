package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.payload.dto.ProductDTO; // ProductDTO import edildi
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        product.setImageUrl(productDetails.getImageUrl()); // Ana resmi de güncelle
        product.setImages(productDetails.getImages());
        product.setSpecifications(productDetails.getSpecifications());
        product.setActive(productDetails.isActive()); // Aktif durumunu da güncelle
        
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    
    // DTO döndüren yeni metod
    public ProductDTO getProductDTOById(Long id) {
        Product product = getProductById(id);
        return ProductDTO.fromEntity(product);
    }

    /**
     * Tüm aktif ürünleri DTO olarak sayfalanmış şekilde getirir.
     * @param pageable Sayfalama bilgisi
     * @return Sayfalanmış ProductDTO listesi
     */
    public Page<ProductDTO> getAllProductsAsDTO(Pageable pageable) {
        Page<Product> productPage = productRepository.findByActiveTrue(pageable);
        return productPage.map(ProductDTO::fromEntity); // Her Product entity'sini ProductDTO'ya çevir
    }
    
    // Eski getAllProducts metodu (Page<Product> döndüren) eğer başka bir yerde kullanılıyorsa kalabilir
    // veya ismi değiştirilebilir (örn: getAllProductEntities).
    public Page<Product> getAllProductEntities(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }


    public Page<ProductDTO> searchProductsAsDTO(String query, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(query, pageable);
        return productPage.map(ProductDTO::fromEntity);
    }

    public Page<ProductDTO> getProductsByCategoryAsDTO(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategory_Id(categoryId, pageable);
        return productPage.map(ProductDTO::fromEntity);
    }
    
    // Benzer ürünler için de DTO dönüşümü eklenebilir
    public List<ProductDTO> getSimilarProductsAsDTO(Long productId, Long categoryId, Pageable pageable) {
        List<Product> products = productRepository.findSimilarProducts(categoryId, productId, pageable);
        return products.stream().map(ProductDTO::fromEntity).collect(Collectors.toList());
    }


    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false); // Ürünü silmek yerine pasif yap
        productRepository.save(product);
    }

    public void updateStock(Long id, int quantityChange) { // quantityChange pozitif (ekleme) veya negatif (çıkarma) olabilir
        Product product = getProductById(id);
        int newStock = product.getStockQuantity() + quantityChange;
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }
        
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    public List<ProductDTO> getLowStockProductsAsDTO() {
        List<Product> products = productRepository.findLowStockProducts(LOW_STOCK_THRESHOLD);
        return products.stream().map(ProductDTO::fromEntity).collect(Collectors.toList());
    }

    public Page<ProductDTO> getTopRatedProductsAsDTO(double minRating, Pageable pageable) {
        Page<Product> productPage = productRepository.findTopRatedProducts(minRating, pageable);
        return productPage.map(ProductDTO::fromEntity);
    }

    public boolean isInStock(Long id, int requestedQuantity) {
        Product product = getProductById(id);
        return product.getStockQuantity() >= requestedQuantity;
    }
}
