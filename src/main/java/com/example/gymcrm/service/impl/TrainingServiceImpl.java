package com.example.gymcrm.service.impl;

import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class TrainingServiceImpl implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainerRepository trainerRepository, TraineeRepository traineeRepository,
                               TrainingTypeRepository trainingTypeRepository) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    @Transactional
    public Training addTraining(AddTrainingRequest request) {
        Trainer trainer = trainerRepository.findByUsername(request.trainerUsername())
                .filter(found -> found.getPassword().equals(request.trainerPassword()))
                .orElseThrow(() -> new AuthenticationException("Invalid trainer credentials"));
        Trainee trainee = traineeRepository.findByUsername(request.traineeUsername())
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + request.traineeUsername()));
        TrainingType type = trainingTypeRepository.findByName(request.trainingType())
                .orElseThrow(() -> new NotFoundException("Training type not found: " + request.trainingType()));

        Training training = new Training(
                null,
                trainee,
                trainer,
                request.trainingName(),
                type,
                request.trainingDate(),
                request.durationMinutes()
        );
        Training saved = trainingRepository.save(training);
        LOGGER.info("Created training with id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> select(Long id) {
        LOGGER.debug("Selecting training with id={}", id);
        return trainingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> selectAll() {
        LOGGER.debug("Selecting all trainings");
        return trainingRepository.findAll();
    }
}
