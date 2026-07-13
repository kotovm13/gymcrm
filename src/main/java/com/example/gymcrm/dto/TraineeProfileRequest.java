package com.example.gymcrm.dto;

import java.time.LocalDate;

public record TraineeProfileRequest(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String address
) {
}
