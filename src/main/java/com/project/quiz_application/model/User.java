package com.project.quiz_application.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @NotBlank(message="Username is required")
    private String username;

    @NotBlank(message="Password cannot be empty")
    private String password;

    @NotBlank(message="Email is required")
    @Email(message="Invalid email format")
    private String email;

    private String role;

    @Override
    public String toString() {
        return "Username: " + username
                + ", Email: " + email
                + ", Role: " + role;
    }
}
