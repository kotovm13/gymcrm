package com.example.gymcrm.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginPasswordChangeRequest(
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "Old password is required")
        String oldPassword,
        @NotBlank(message = "New password is required")
        String newPassword
) {
}
