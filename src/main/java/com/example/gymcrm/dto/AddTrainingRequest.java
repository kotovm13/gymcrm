package com.example.gymcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AddTrainingRequest(
        @NotBlank(message = "Trainer username is required")
        String trainerUsername,
        @NotBlank(message = "Trainer password is required")
        String trainerPassword,
        @NotBlank(message = "Trainee username is required")
        String traineeUsername,
        @NotBlank(message = "Training name is required")
        String trainingName,
        @NotBlank(message = "Training type is required")
        String trainingType,
        @NotNull(message = "Training date is required")
        LocalDate trainingDate,
        @Positive(message = "Training duration must be positive")
        int durationMinutes
) {
}
