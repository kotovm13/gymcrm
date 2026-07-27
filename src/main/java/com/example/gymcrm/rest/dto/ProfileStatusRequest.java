package com.example.gymcrm.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileStatusRequest(
        @NotBlank(message = "Username is required")
        String username,
        @NotNull(message = "Active state is required")
        Boolean active
) {
}
