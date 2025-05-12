package com.ecommerce.backend.payload.dto;

import lombok.Data;

@Data
public class ProductCreateDTO {
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String description;
    private Boolean active;
} 