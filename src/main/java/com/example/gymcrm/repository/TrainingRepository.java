package com.example.gymcrm.repository;

import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.dto.TrainerTrainingCriteria;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);
    Optional<Training> findById(Long id);
    List<Training> findAll();
    List<Training> findByTraineeCriteria(String traineeUsername, TraineeTrainingCriteria criteria);
    List<Training> findByTrainerCriteria(String trainerUsername, TrainerTrainingCriteria criteria);
}
