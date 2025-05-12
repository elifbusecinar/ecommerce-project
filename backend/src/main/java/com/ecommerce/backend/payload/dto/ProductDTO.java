package com.ecommerce.backend.payload.dto;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.Category; // Kategori bilgisi için import
import lombok.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl; // Ana resim URL'i
    private List<String> images; // Ek resim URL'leri listesi
    private String categoryName; // Kategori adı
    private Long categoryId; // Kategori ID'si
    private Double rating;
    private boolean active;
    private String createdAt;
    private String updatedAt;

    // Tarih formatlayıcı (UserDTO'daki gibi)
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl()) // Ana resim
                .images(product.getImages()) // Ek resimler listesi
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .rating(product.getRating())
                .active(product.isActive())
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().format(formatter) : null)
                .updatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().format(formatter) : null)
                .build();
    }

    // DTO -> Entity dönüşümü genellikle admin panelinde yeni ürün eklerken veya güncellerken kullanılır.
    // Şimdilik sadece listeleme için fromEntity yeterli.
    // public Product toEntity() {
    //     Product product = new Product();
    //     product.setId(this.id);
    //     product.setName(this.name);
    //     // ... diğer alanlar
    //     return product;
    // }
}
