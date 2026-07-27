package com.example.gymcrm.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RestAddTrainingRequest(
        @NotBlank(message = "Trainee username is required")
        String traineeUsername,
        @NotBlank(message = "Trainer username is required")
        String trainerUsername,
        @NotBlank(message = "Training name is required")
        String trainingName,
        @NotNull(message = "Training date is required")
        LocalDate trainingDate,
        @Positive(message = "Training duration must be positive")
        int durationMinutes
) {
}
