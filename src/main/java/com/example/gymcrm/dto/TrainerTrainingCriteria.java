package com.example.gymcrm.dto;

import java.time.LocalDate;

public record TrainerTrainingCriteria(
        LocalDate fromDate,
        LocalDate toDate,
        String traineeName
) {
}
