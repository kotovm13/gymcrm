package com.example.gymcrm.dto;

import java.time.LocalDate;

public record TraineeTrainingCriteria(
        LocalDate fromDate,
        LocalDate toDate,
        String trainerName,
        String trainingType
) {
}
