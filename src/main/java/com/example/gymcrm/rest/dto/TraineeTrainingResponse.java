package com.example.gymcrm.rest.dto;

import java.time.LocalDate;

public record TraineeTrainingResponse(
        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        int durationMinutes,
        String trainerName
) {
}
