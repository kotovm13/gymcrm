package com.example.gymcrm.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TraineeTrainersUpdateRequest(
        @NotBlank(message = "Trainee username is required")
        String traineeUsername,
        @NotEmpty(message = "Trainers list is required")
        List<@Valid TrainerUsernameRequest> trainers
) {
}
