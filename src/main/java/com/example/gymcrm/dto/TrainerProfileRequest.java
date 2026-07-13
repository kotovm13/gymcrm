package com.example.gymcrm.dto;

public record TrainerProfileRequest(
        String firstName,
        String lastName,
        String specialization
) {
}
