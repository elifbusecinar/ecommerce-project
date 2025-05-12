package com.ecommerce.backend.controller;

import com.ecommerce.backend.exception.ForbiddenException; // ForbiddenException import edildi
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.payload.dto.UserDTO; // UserDTO import edildi
// import com.ecommerce.backend.repository.UserRepository; // Direkt kullanılmıyorsa kaldırılabilir
import com.ecommerce.backend.security.UserDetailsImpl;
import com.ecommerce.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users") // API base path is /api/users due to server.servlet.context-path=/api
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "User", description = "User management APIs")
public class UserController {
    private final UserService userService;
    // private final UserRepository userRepository; // UserService üzerinden erişim tercih edilir.

    @Operation(summary = "Get all users", description = "Retrieves a list of all users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // UserService'deki DTO döndüren metodu çağır
        List<UserDTO> userDTOs = userService.getAllUsersAsDTO();
        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        boolean isAdmin = principal.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        
        User userEntity = userService.getUserById(id); // Get the User entity

        if (isAdmin || principal.getId().equals(userEntity.getId())) {
            return ResponseEntity.ok(UserDTO.fromEntity(userEntity)); // Convert entity to DTO for response
        } else {
            throw new ForbiddenException("You are not authorized to view this profile.");
        }
    }

    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User userEntity = userService.getUserByEmail(userDetails.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userDetails.getEmail()));
        return ResponseEntity.ok(UserDTO.fromEntity(userEntity)); // Convert entity to DTO
    }

    @Operation(summary = "Update user", description = "Updates an existing user's details. Admin can update any user, regular users can only update their own profile.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (#id == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails, // For simplicity, still taking User entity. Ideally, this should be a UserUpdateDTO.
            Authentication authentication) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser)); // Convert updated entity to DTO
    }

    @Operation(summary = "Update user password", description = "Updates the password for the currently authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid password data or current password mismatch"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/password")
    // Ensure only the authenticated user can change their own password, or an admin for any user (if business logic allows)
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
    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable Long userId,
            @RequestBody Role roleRequest) { // Role object might be simple, e.g., just containing the name
        userService.addRoleToUser(userId, roleRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove role from user", description = "Removes a role from a user (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @RequestBody Role roleRequest) { // Role object might be simple
        userService.removeRoleFromUser(userId, roleRequest);
        return ResponseEntity.ok().build();
    }

    // --- Admin specific queries returning List<User> should also be converted to List<UserDTO> ---
    // For brevity, I'm leaving them as they were, but they are prone to the same JSON serialization issue.
    // Ideally, these should also call service methods that return List<UserDTO>.

    @Operation(summary = "Get users by role", description = "Retrieves users filtered by a specific role (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String roleName) {
        List<User> users = userService.getUsersByRole(roleName);
        List<UserDTO> userDTOs = users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Get active users", description = "Retrieves a list of all active users (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        List<UserDTO> userDTOs = users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Get inactive users", description = "Retrieves users inactive since a threshold (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getInactiveUsers(
            @RequestParam LocalDateTime threshold) {
        List<User> users = userService.getInactiveUsers(threshold);
        List<UserDTO> userDTOs = users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Get users with no roles", description = "Retrieves users who have no roles assigned (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/no-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersWithNoRoles() {
        List<User> users = userService.getUsersWithNoRoles();
        List<UserDTO> userDTOs = users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @Operation(summary = "Get all admins", description = "Retrieves a list of all admin users (Admin only)")
    @ApiResponses(value = { /* ... */ })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllAdmins() {
        List<User> users = userService.getAllAdmins();
        List<UserDTO> userDTOs = users.stream().map(UserDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
}
