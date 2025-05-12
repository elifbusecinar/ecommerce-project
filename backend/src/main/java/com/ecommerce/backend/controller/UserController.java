package com.ecommerce.backend.controller;

import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.UserDetailsImpl;
import com.ecommerce.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation; // Swagger importları
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "User", description = "User management APIs") // Swagger Tag
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "Get all users", description = "Retrieves a list of all users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserById(@PathVariable Long id, Authentication authentication) {
        // Kullanıcının kendi profilini veya adminin herhangi bir kullanıcıyı görmesine izin ver
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        boolean isAdmin = principal.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        User requestedUser = userService.getUserById(id);

        if (isAdmin || principal.getId().equals(requestedUser.getId())) {
            return ResponseEntity.ok(requestedUser);
        } else {
            // ForbiddenException fırlatılabilir veya ResponseEntity.status(HttpStatus.FORBIDDEN).build() dönülebilir.
            // GlobalExceptionHandler bunu yakalayacaktır.
            throw new com.ecommerce.backend.exception.ForbiddenException("You are not authorized to view this profile.");
        }
    }

    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getEmail()) // getUserByEmail olarak değiştirildi
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userDetails.getEmail()));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user", description = "Updates an existing user's details")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (#id == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @Operation(summary = "Update user password", description = "Updates the password for a specific user")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated() and #id == authentication.principal.id")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            Authentication authentication) {
        userService.updatePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Deactivate user", description = "Deactivates a user account (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate user", description = "Activates a user account (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add role to user", description = "Adds a role to a user (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{userId}/roles") // @PutMapping'den @PostMapping'e değiştirildi, genellikle yeni bir ilişki eklemek için POST kullanılır.
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable Long userId,
            @RequestBody Role role) { // RequestBody'den rol adı veya ID'si almak daha iyi olabilir. Şimdilik Role objesi alıyor.
        userService.addRoleToUser(userId, role);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove role from user", description = "Removes a role from a user (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @RequestBody Role role) { // RequestBody'den rol adı veya ID'si almak daha iyi olabilir.
        userService.removeRoleFromUser(userId, role);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Get users by role", description = "Retrieves users filtered by a specific role (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(userService.getUsersByRole(roleName));
    }

    @Operation(summary = "Get active users", description = "Retrieves a list of all active users (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @Operation(summary = "Get inactive users", description = "Retrieves users inactive since a threshold (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getInactiveUsers(
            @RequestParam LocalDateTime threshold) {
        return ResponseEntity.ok(userService.getInactiveUsers(threshold));
    }

    @Operation(summary = "Get users with no roles", description = "Retrieves users who have no roles assigned (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/no-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersWithNoRoles() {
        return ResponseEntity.ok(userService.getUsersWithNoRoles());
    }

    @Operation(summary = "Get all admins", description = "Retrieves a list of all admin users (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(userService.getAllAdmins());
    }
}
