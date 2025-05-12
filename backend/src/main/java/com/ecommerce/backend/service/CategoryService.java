package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.exception.BadRequestException; // Eklendi
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new BadRequestException("Category with name '" + category.getName() + "' already exists.");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // İsim değişikliği varsa ve yeni isim zaten kullanımdaysa hata ver
        if (!existingCategory.getName().equals(categoryDetails.getName()) &&
            categoryRepository.existsByName(categoryDetails.getName())) {
            throw new BadRequestException("Category with name '" + categoryDetails.getName() + "' already exists.");
        }

        existingCategory.setName(categoryDetails.getName());
        // Diğer alanlar da güncellenebilir (örn: description)
        // existingCategory.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        // Kategoriye bağlı ürünler varsa ne yapılacağına karar verilmeli.
        // Şimdilik direkt silme işlemi yapıyoruz.
        // Alternatif olarak, kategoriye bağlı ürün varsa silmeyi engelleyebilir veya ürünlerin kategorisini null yapabiliriz.
        categoryRepository.deleteById(id);
    }
}