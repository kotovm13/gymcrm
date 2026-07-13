package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final TrainingTypeDao trainingTypeDao;

    public TrainingServiceImpl(TrainingDao trainingDao, TrainerDao trainerDao, TraineeDao traineeDao,
                               TrainingTypeDao trainingTypeDao) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    @Transactional
    public Training create(Training training) {
        ValidationUtils.validateTraining(training);
        Training saved = trainingDao.save(training);
        LOGGER.info("Created training with id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Training addTraining(String trainerUsername, String trainerPassword, String traineeUsername,
                                String trainingName, String trainingType, LocalDate trainingDate, int durationMinutes) {
        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .filter(found -> found.getPassword().equals(trainerPassword))
                .orElseThrow(() -> new AuthenticationException("Invalid trainer credentials"));
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + traineeUsername));
        TrainingType type = trainingTypeDao.findByName(trainingType)
                .orElseThrow(() -> new NotFoundException("Training type not found: " + trainingType));

        Training training = new Training(null, trainee, trainer, trainingName, type, trainingDate, durationMinutes);
        return create(training);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> select(Long id) {
        LOGGER.debug("Selecting training with id={}", id);
        return trainingDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> selectAll() {
        LOGGER.debug("Selecting all trainings");
        return trainingDao.findAll();
    }
}
