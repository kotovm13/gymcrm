package com.example.gymcrm.service;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;

public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static void validateTraineeForCreateOrUpdate(Trainee trainee) {
        requireText(trainee.getFirstName(), "First name is required");
        requireText(trainee.getLastName(), "Last name is required");
    }

    public static void validateTrainerForCreateOrUpdate(Trainer trainer) {
        requireText(trainer.getFirstName(), "First name is required");
        requireText(trainer.getLastName(), "Last name is required");
        if (trainer.getSpecialization() == null) {
            throw new IllegalArgumentException("Trainer specialization is required");
        }
    }

    public static void validateTraining(Training training) {
        requireText(training.getTrainingName(), "Training name is required");
        if (training.getTrainee() == null) {
            throw new IllegalArgumentException("Training trainee is required");
        }
        if (training.getTrainer() == null) {
            throw new IllegalArgumentException("Training trainer is required");
        }
        if (training.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type is required");
        }
        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }
        if (training.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }

    public static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireStateChange(boolean currentState, boolean newState, String profileType) {
        if (currentState == newState) {
            throw new IllegalStateException(profileType + " active state is already " + newState);
        }
    }
}
