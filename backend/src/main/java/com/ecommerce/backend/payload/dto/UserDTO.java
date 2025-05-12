package com.ecommerce.backend.payload.dto;

import com.ecommerce.backend.model.User; // User entity'sini import et
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private String lastLogin; // Tarihleri string olarak formatlayacağız
    private Set<String> roles; // Rol isimlerinin bir seti
    private String createdAt;
    private String updatedAt;
    private String phoneNumber; // Eksikse eklendi

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.isActive())
                .lastLogin(user.getLastLogin() != null ? user.getLastLogin().format(formatter) : null)
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name()) // Role enum'ının adını al
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().format(formatter) : null)
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
