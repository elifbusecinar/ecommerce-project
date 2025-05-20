package com.ecommerce.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService {

    @Value("${file.upload-dir:./uploads/images/products}") // application.properties'ten alınabilir
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}") // API'nizin temel URL'i
    private String appBaseUrl;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;


    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        File uploadPathDir = new File(uploadDir);
        if (!uploadPathDir.exists()) {
            uploadPathDir.mkdirs();
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        try {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        } catch (Exception e) {
            // ignore
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension;
        Path targetLocation = Paths.get(uploadDir).resolve(newFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // API context path'ini ve base URL'i kullanarak tam URL oluştur
        // Örneğin: http://localhost:8080/api/uploads/images/products/uuid.jpg
        // Eğer statik kaynak sunumu için /uploads/** gibi bir mapping varsa bu daha basit olabilir.
        // Şimdilik sadece göreceli yolu döndürüyoruz, ProductController'da birleştirilecek.
        return "/uploads/images/products/" + newFileName; // API üzerinden erişilecek göreceli yol
    }

    @Override
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return false;
        }
        try {
            // filePath'in göreceli bir yol olduğunu varsayıyoruz (örn: /uploads/images/products/uuid.jpg)
            // Bu yolun başındaki '/' karakterini ve varsa contextPath'i kaldırarak dosya sistemindeki gerçek yolu bulmalıyız.
            String systemPath = filePath;
            if (systemPath.startsWith(contextPath)) {
                systemPath = systemPath.substring(contextPath.length());
            }
            if (systemPath.startsWith("/")) {
                 systemPath = systemPath.substring(1);
            }

            Path pathToDelete = Paths.get(systemPath); // Eğer filePath zaten ./uploads/images/products/uuid.jpg ise direkt bu
            // Path pathToDelete = Paths.get(uploadDir).resolve(Paths.get(filePath).getFileName()); // Daha güvenli bir yol

            return Files.deleteIfExists(pathToDelete);
        } catch (IOException e) {
            // Log error
            return false;
        }
    }

    @Override
    public String getImageUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return null;
        }
        // Eğer contextPath varsa ve relativePath bununla başlamıyorsa, ekleyebiliriz.
        // Ancak genellikle frontend'e sadece relative path gönderilir ve frontend base URL ile birleştirir.
        // Backend'in statik kaynakları sunması durumunda bu URL'in doğru olması önemlidir.
        // Örneğin, "/api/uploads/images/products/uuid.jpg"
        return appBaseUrl + (contextPath.endsWith("/") || relativePath.startsWith("/") ? contextPath : contextPath + "/") +
               (relativePath.startsWith("/") ? relativePath.substring(1) : relativePath);
    }
}