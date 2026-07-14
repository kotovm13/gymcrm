package com.example.gymcrm.service;

import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.AddTrainingRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training addTraining(@Valid AddTrainingRequest request);
    Optional<Training> select(Long id);
    List<Training> selectAll();
}
