package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.payload.dto.ProductDTO; // ProductDTO import edildi
import com.ecommerce.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Get all products",
        description = "Retrieves a paginated list of all active products as DTOs"
    )
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        // DTO döndüren servis metodunu çağır
        return ResponseEntity.ok(productService.getAllProductsAsDTO(pageable));
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a specific product by its ID as DTO"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
        @Parameter(description = "ID of the product to retrieve") @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductDTOById(id)); // DTO döndüren metod
    }

    @Operation(
        summary = "Search products",
        description = "Search products by query string with pagination, returns DTOs"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
        @Parameter(description = "Search query string") @RequestParam String query,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.searchProductsAsDTO(query, pageable));
    }

    @Operation(
        summary = "Get products by category",
        description = "Retrieves products filtered by category ID with pagination, returns DTOs"
    )
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
        @Parameter(description = "ID of the category to filter by") @PathVariable Long categoryId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getProductsByCategoryAsDTO(categoryId, pageable));
    }
    
    @Operation(
        summary = "Get similar products",
        description = "Retrieves products similar to the specified product as DTOs"
    )
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<ProductDTO>> getSimilarProducts(
        @Parameter(description = "ID of the product to find similar items for") @PathVariable Long id,
        @Parameter(description = "Category ID for filtering similar products") @RequestParam Long categoryId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getSimilarProductsAsDTO(id, categoryId, pageable));
    }

    @Operation(
        summary = "Get top rated products",
        description = "Retrieves products with ratings above the specified minimum as DTOs"
    )
    @GetMapping("/top-rated")
    public ResponseEntity<Page<ProductDTO>> getTopRatedProducts(
        @Parameter(description = "Minimum rating threshold (default: 4.0)") 
        @RequestParam(defaultValue = "4.0") double minRating,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getTopRatedProductsAsDTO(minRating, pageable));
    }

    @Operation(
        summary = "Create new product",
        description = "Creates a new product (Admin only)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
        @ModelAttribute com.ecommerce.backend.payload.dto.ProductCreateDTO productDto,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String uploadDir = "uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();
            String filePath = uploadDir + image.getOriginalFilename();
            try {
                image.transferTo(new File(filePath));
                imageUrl = "/" + filePath;
            } catch (Exception e) {
                throw new RuntimeException("Image upload failed", e);
            }
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(BigDecimal.valueOf(productDto.getPrice()));
        product.setStockQuantity(productDto.getStockQuantity());
        product.setDescription(productDto.getDescription());
        product.setActive(productDto.getActive() != null ? productDto.getActive() : true);
        product.setImageUrl(imageUrl);

        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(ProductDTO.fromEntity(createdProduct));
    }

    @Operation(
        summary = "Update product",
        description = "Updates an existing product (Admin only)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct( // ProductDTO döndür
        @Parameter(description = "ID of the product to update") @PathVariable Long id,
        @Parameter(description = "Updated product details") @RequestBody Product productDetails // Şimdilik Product alıyor, ProductUpdateDTO daha iyi olurdu
    ) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct)); // DTO'ya çevir
    }

    @Operation(
        summary = "Delete product (Deactivate)",
        description = "Deactivates a product (Admin only)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "ID of the product to deactivate") @PathVariable Long id
    ) {
        productService.deleteProduct(id); // Bu metod ürünü pasif yapar
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Update product stock",
        description = "Updates the stock quantity of a product (Admin only)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStock(
        @Parameter(description = "ID of the product to update stock for") @PathVariable Long id,
        @Parameter(description = "Quantity to add (positive) or remove (negative)") @RequestParam int quantityChange
    ) {
        productService.updateStock(id, quantityChange);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get low stock products",
        description = "Retrieves products with low stock levels as DTOs (Admin only)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProductsAsDTO());
    }
}
