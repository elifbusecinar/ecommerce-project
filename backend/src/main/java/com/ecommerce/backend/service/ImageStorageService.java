package com.ecommerce.backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageStorageService {
    /**
     * Verilen MultipartFile'ı saklar ve saklanan dosyanın erişilebilir URL'ini döndürür.
     *
     * @param file Yüklenecek görsel dosyası.
     * @return Görselin erişilebilir URL'i veya null (hata durumunda).
     * @throws IOException Dosya işlemleri sırasında bir hata oluşursa.
     */
    String storeFile(MultipartFile file) throws IOException;

    /**
     * Verilen dosya yolundaki bir görseli siler.
     *
     * @param filePath Silinecek görselin yolu.
     * @return Silme başarılıysa true, aksi takdirde false.
     */
    boolean deleteFile(String filePath);

    /**
     * Görselin tam yolunu (URL) döndürür.
     * @param relativePath Görselin göreceli yolu (örn: /uploads/image.png)
     * @return Görselin tam erişim URL'si
     */
    String getImageUrl(String relativePath);
}