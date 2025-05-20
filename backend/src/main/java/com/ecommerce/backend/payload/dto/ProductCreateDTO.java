package com.ecommerce.backend.payload.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ProductCreateDTO {

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price; // BigDecimal olarak güncellendi

    @NotNull(message = "Stock quantity cannot be null")
    @Positive(message = "Stock quantity must be a positive number")
    private Integer stockQuantity;

    @Size(max = 1000, message = "Description can be at most 1000 characters")
    private String description;

    private Boolean active; // Varsayılan olarak true ayarlanabilir veya frontend'den alınabilir

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId; // Kategori ID'si eklendi
}