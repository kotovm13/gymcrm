package com.example.gymcrm.service;

import com.example.gymcrm.domain.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training create(Training training);
    Training addTraining(String trainerUsername, String trainerPassword, String traineeUsername,
                         String trainingName, String trainingType, LocalDate trainingDate, int durationMinutes);
    Optional<Training> select(Long id);
    List<Training> selectAll();
}
