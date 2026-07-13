package com.example.gymcrm.dto;

import java.time.LocalDate;

public record AddTrainingRequest(
        String trainerUsername,
        String trainerPassword,
        String traineeUsername,
        String trainingName,
        String trainingType,
        LocalDate trainingDate,
        int durationMinutes
) {
}
