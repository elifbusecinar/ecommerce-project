package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.payload.dto.ProductCreateDTO;
import com.ecommerce.backend.payload.dto.ProductDTO;
import com.ecommerce.backend.payload.dto.ProductUpdateDTO; // Yeni DTO
import com.ecommerce.backend.service.CategoryService; // Kategori servisi
import com.ecommerce.backend.service.ImageStorageService; // Yeni servis
import com.ecommerce.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // @Valid eklendi
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // IOException eklendi
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products") // API base path /api/products olacak (application.properties'e göre)
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService; // Kategori servisi eklendi
    private final ImageStorageService imageStorageService; // Görsel saklama servisi eklendi

    @Operation(summary = "Get all active products", description = "Retrieves a paginated list of all active products as DTOs")
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsAsDTO(pageable));
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID as DTO")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDTOById(id));
    }

    @Operation(summary = "Search products", description = "Search products by query string with pagination, returns DTOs")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(@RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProductsAsDTO(query, pageable));
    }

    @Operation(summary = "Get products by category", description = "Retrieves products filtered by category ID with pagination, returns DTOs")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategoryAsDTO(categoryId, pageable));
    }

    @Operation(summary = "Get similar products", description = "Retrieves products similar to the specified product as DTOs")
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<ProductDTO>> getSimilarProducts(@PathVariable Long id, @RequestParam Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(productService.getSimilarProductsAsDTO(id, categoryId, pageable));
    }

    @Operation(summary = "Get top rated products", description = "Retrieves products with ratings above the specified minimum as DTOs")
    @GetMapping("/top-rated")
    public ResponseEntity<Page<ProductDTO>> getTopRatedProducts(@RequestParam(defaultValue = "4.0") double minRating, Pageable pageable) {
        return ResponseEntity.ok(productService.getTopRatedProductsAsDTO(minRating, pageable));
    }

    @Operation(summary = "Create new product", description = "Creates a new product (Admin only). Image is optional.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product created successfully", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data or image upload failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "Product creation data", required = true) @Valid @ModelAttribute ProductCreateDTO productCreateDTO,
            @Parameter(description = "Product image file (optional)") @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Product createdProduct = productService.createProductWithImage(productCreateDTO, imageFile);
            return ResponseEntity.ok(ProductDTO.fromEntity(createdProduct));
        } catch (IOException e) {
            // Log error
            throw new RuntimeException("Failed to create product with image: " + e.getMessage(), e);
        }
    }


    @Operation(summary = "Update product", description = "Updates an existing product (Admin only). Image update is handled separately or as part of full update.")
     @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Product or Category not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Multipart form data kabul edecek şekilde güncellendi
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID of the product to update") @PathVariable Long id,
            @Parameter(description = "Product update data", required = true) @Valid @ModelAttribute ProductUpdateDTO productUpdateDTO, // @RequestBody yerine @ModelAttribute
            @Parameter(description = "New product image file (optional)") @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            Product updatedProduct = productService.updateProductWithImage(id, productUpdateDTO, imageFile);
            return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update product with image: " + e.getMessage(), e);
        }
    }


    @Operation(summary = "Delete product (Deactivate)", description = "Deactivates a product (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deactivateProduct(id); // Deactivate olarak güncellendi
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate product", description = "Activates a previously deactivated product (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateProduct(@PathVariable Long id) {
        productService.activateProduct(id);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Update product stock", description = "Updates the stock quantity of a product (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable Long id,
            @RequestParam int quantityChange) {
        Product product = productService.updateStock(id, quantityChange);
        return ResponseEntity.ok(ProductDTO.fromEntity(product));
    }

    @Operation(summary = "Get low stock products", description = "Retrieves products with low stock levels as DTOs (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        List<Product> products = productService.getLowStockProducts(5); // Eşik değeri parametre olarak alınabilir
        List<ProductDTO> dtos = products.stream().map(ProductDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}