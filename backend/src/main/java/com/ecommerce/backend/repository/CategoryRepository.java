// Path: backend/src/main/java/com/ecommerce/backend/repository/CategoryRepository.java
package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Verilen isme sahip bir kategoriyi bulur.
     * Kategori isimlerinin benzersiz (unique) olduğunu varsayarsak kullanışlıdır.
     *
     * @param name Kategorinin ismi.
     * @return Opsiyonel olarak bulunan Category nesnesi.
     */
    Optional<Category> findByName(String name);

    /**
     * Verilen isme sahip bir kategorinin var olup olmadığını kontrol eder (büyük/küçük harf duyarsız).
     * Yeni kategori eklerken isim çakışmalarını önlemek için kullanılabilir.
     *
     * @param name Kontrol edilecek kategori ismi.
     * @return Eğer bu isimde bir kategori varsa true, yoksa false.
     */
    boolean existsByNameIgnoreCase(String name);

    // Gelecekte ihtiyacınız olabilecek diğer özel sorgu metodlarını buraya ekleyebilirsiniz.
    // Örneğin:
    // List<Category> findByParentCategoryIsNull(); // Ana kategorileri bulmak için
    // List<Category> findByNameContainingIgnoreCase(String keyword); // İsimde arama yapmak için
}