package com.ecommerce.backend.payload.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles; // ROLE_USER, ROLE_ADMIN vs.
}
