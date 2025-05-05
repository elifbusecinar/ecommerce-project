package com.ecommerce.backend.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User registration request payload")
public class RegisterRequest {
    @Schema(description = "User's first name", example = "John", required = true)
    private String firstName;

    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    private String email;

    @Schema(description = "User's username", example = "johndoe", required = true)
    private String username;

    @Schema(description = "User's password", example = "password123", required = true)
    private String password;
}
