package com.ecommerce.backend.payload.dto;

import lombok.Data;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateDTO {

    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description can be at most 1000 characters")
    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Positive(message = "Stock quantity must be a positive number")
    private Integer stockQuantity;

    private Long categoryId;

    private List<String> images; // Mevcut resim URL'leri veya yeni eklenecekler

    private Boolean active;

    // Güncellenmesini istemediğiniz alanları buraya eklemeyin
    // Örneğin, rating, reviews vb.
}