package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.payload.dto.ProductCreateDTO;
import com.ecommerce.backend.payload.dto.ProductDTO;
import com.ecommerce.backend.payload.dto.ProductUpdateDTO;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Kategori repository eklendi
    private final ImageStorageService imageStorageService; // Görsel saklama servisi eklendi

    @Value("${product.low-stock-threshold:5}") // application.properties'ten alınabilir
    private int lowStockThreshold;

    public Product createProductWithImage(ProductCreateDTO productCreateDTO, MultipartFile imageFile) throws IOException {
        Product product = new Product();
        product.setName(productCreateDTO.getName());
        product.setDescription(productCreateDTO.getDescription());
        product.setPrice(productCreateDTO.getPrice());
        product.setStockQuantity(productCreateDTO.getStockQuantity());
        product.setActive(productCreateDTO.getActive() != null ? productCreateDTO.getActive() : true);

        if (productCreateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productCreateDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productCreateDTO.getCategoryId()));
            product.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageStorageService.storeFile(imageFile);
            product.setImageUrl(imageUrl); // Ana görsel olarak ayarla
            // İlk görseli images listesine de ekleyebiliriz
            if (imageUrl != null) {
                 product.getImages().add(imageUrl);
            }
        }
        return productRepository.save(product);
    }

    public Product updateProductWithImage(Long id, ProductUpdateDTO productUpdateDTO, MultipartFile imageFile) throws IOException {
        Product product = getProductById(id);

        if (productUpdateDTO.getName() != null) {
            product.setName(productUpdateDTO.getName());
        }
        if (productUpdateDTO.getDescription() != null) {
            product.setDescription(productUpdateDTO.getDescription());
        }
        if (productUpdateDTO.getPrice() != null) {
            product.setPrice(productUpdateDTO.getPrice());
        }
        if (productUpdateDTO.getStockQuantity() != null) {
            product.setStockQuantity(productUpdateDTO.getStockQuantity());
        }
        if (productUpdateDTO.getActive() != null) {
            product.setActive(productUpdateDTO.getActive());
        }

        if (productUpdateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productUpdateDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productUpdateDTO.getCategoryId()));
            product.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            // Eski resmi sil (eğer varsa ve farklıysa)
            if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
                imageStorageService.deleteFile(product.getImageUrl());
            }
            String newImageUrl = imageStorageService.storeFile(imageFile);
            product.setImageUrl(newImageUrl);
            // Ana resmi images listesinde de güncelleyebiliriz veya yeni ekleyebiliriz.
            // Örnek: product.getImages().clear(); product.getImages().add(newImageUrl);
        } else if (productUpdateDTO.getImages() != null) { // Frontend'den sadece URL listesi gelirse
             product.setImages(productUpdateDTO.getImages());
             if (productUpdateDTO.getImages() != null && !productUpdateDTO.getImages().isEmpty()){
                 product.setImageUrl(productUpdateDTO.getImages().get(0)); // İlk resmi ana resim yap
             } else {
                 product.setImageUrl(null);
             }
        }


        return productRepository.save(product);
    }


    // Bu metod ProductController'da ProductCreateDTO ve MultipartFile alacak şekilde güncellenmeli.
    // Şimdilik Product entity alıyor.
    public Product createProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + product.getCategory().getId()));
            product.setCategory(category);
        }
        product.setActive(true); // Yeni ürünler varsayılan olarak aktif
        return productRepository.save(product);
    }

    // Bu metod ProductController'da ProductUpdateDTO ve opsiyonel MultipartFile alacak şekilde güncellenmeli.
    public Product updateProduct(Long id, Product productDetails) { // productDetails ProductUpdateDTO olmalı
        Product product = getProductById(id);

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDetails.getCategory().getId()));
            product.setCategory(category);
        } else {
            product.setCategory(null); // Kategori kaldırılıyorsa
        }
        product.setImageUrl(productDetails.getImageUrl());
        product.setImages(productDetails.getImages());
        product.setSpecifications(productDetails.getSpecifications());
        product.setActive(productDetails.isActive());

        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public ProductDTO getProductDTOById(Long id) {
        Product product = getProductById(id);
        return ProductDTO.fromEntity(product);
    }

    public Page<ProductDTO> getAllProductsAsDTO(Pageable pageable) {
        Page<Product> productPage = productRepository.findByActiveTrue(pageable);
        return productPage.map(ProductDTO::fromEntity);
    }

    public Page<ProductDTO> searchProductsAsDTO(String query, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(query, pageable);
        return productPage.map(ProductDTO::fromEntity);
    }

    public Page<ProductDTO> getProductsByCategoryAsDTO(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        Page<Product> productPage = productRepository.findByCategoryAndActiveTrue(category, pageable); // Sadece aktif ürünler
        return productPage.map(ProductDTO::fromEntity);
    }

    public List<ProductDTO> getSimilarProductsAsDTO(Long productId, Long categoryId, Pageable pageable) {
        Product currentProduct = getProductById(productId); // Mevcut ürünü kontrol et
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        List<Product> products = productRepository.findByCategoryAndIdNotAndActiveTrue(category, productId, pageable);
        return products.stream().map(ProductDTO::fromEntity).collect(Collectors.toList());
    }

    public void deactivateProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public void activateProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(true);
        productRepository.save(product);
    }

    public Product updateStock(Long id, int quantityChange) {
        Product product = getProductById(id);
        int newStock = product.getStockQuantity() + quantityChange;

        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }
     public List<ProductDTO> getLowStockProductsAsDTO() { // Bu metod zaten vardı, threshold parametresi ekleyebiliriz.
        List<Product> products = productRepository.findLowStockProducts(lowStockThreshold);
        return products.stream().map(ProductDTO::fromEntity).collect(Collectors.toList());
    }


    public Page<ProductDTO> getTopRatedProductsAsDTO(double minRating, Pageable pageable) {
        Page<Product> productPage = productRepository.findTopRatedProducts(minRating, pageable);
        return productPage.map(ProductDTO::fromEntity);
    }

    public boolean isInStock(Long id, int requestedQuantity) {
        Product product = getProductById(id);
        return product.isActive() && product.getStockQuantity() >= requestedQuantity;
    }
}