package com.example.gymcrm.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerUpdateRequest(
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotNull(message = "Active state is required")
        Boolean active
) {
}
