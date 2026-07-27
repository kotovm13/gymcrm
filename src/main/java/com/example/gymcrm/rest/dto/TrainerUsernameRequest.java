package com.example.gymcrm.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record TrainerUsernameRequest(
        @NotBlank(message = "Trainer username is required")
        String username
) {
}
