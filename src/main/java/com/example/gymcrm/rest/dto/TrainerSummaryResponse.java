package com.example.gymcrm.rest.dto;

public record TrainerSummaryResponse(
        String username,
        String firstName,
        String lastName,
        String specialization
) {
}
