package com.example.gymcrm.dto;

import jakarta.validation.constraints.NotBlank;

public record TrainerProfileRequest(
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotBlank(message = "Trainer specialization is required")
        String specialization
) {
}
