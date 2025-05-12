package com.ecommerce.backend.controller;

import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository; // UserRepository'yi import et
import com.ecommerce.backend.security.UserDetailsImpl;
import com.ecommerce.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users") // server.servlet.context-path=/api ile birlikte /api/users olacak
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository; // Profil endpoint'i için eklendi

    // UserController İÇİNDEKİ @PostMapping("/register") METODU KALDIRILDI.

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // Yetkilendirme kontrolü eklenebilir: Sadece admin veya kullanıcının kendisi erişebilmeli.
        // Örnek: if (!authentication.getName().equals(userService.getUserById(id).getEmail()) && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) { throw new AccessDeniedException("Forbidden"); }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // userDetails.getUsername() aslında UserDetailsServiceImpl'de user.getEmail() ile set ediliyor JWT subject'i olarak.
        // Bu yüzden findByEmail kullanmak daha doğru.
        User user = userRepository.findByEmail(userDetails.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userDetails.getEmail()));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (#id == authentication.principal.id or hasRole('ADMIN'))") // Örnek yetkilendirme
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails,
            Authentication authentication) { // Authentication parametresi eklendi
        // Yetkilendirme yukarıdaki @PreAuthorize ile yapıldı, ya da burada manuel yapılabilir.
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated() and #id == authentication.principal.id") // Sadece kendi şifresini değiştirebilmeli
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            Authentication authentication) { // Authentication parametresi eklendi
        userService.updatePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    // Rol yönetimi endpointleri admin yetkisine bağlı kalmalı
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable Long userId,
            @RequestBody Role role) {
        userService.addRoleToUser(userId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @RequestBody Role role) {
        userService.removeRoleFromUser(userId, role);
        return ResponseEntity.ok().build();
    }

    // Diğer admin endpointleri olduğu gibi kalabilir
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(userService.getUsersByRole(roleName));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getInactiveUsers(
            @RequestParam LocalDateTime threshold) {
        return ResponseEntity.ok(userService.getInactiveUsers(threshold));
    }

    @GetMapping("/no-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersWithNoRoles() {
        return ResponseEntity.ok(userService.getUsersWithNoRoles());
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(userService.getAllAdmins());
    }
}