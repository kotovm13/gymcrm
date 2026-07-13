package com.example.gymcrm.service.impl;

import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;
    private final ProfileCredentialsGenerator credentialsGenerator;

    public TraineeServiceImpl(TraineeDao traineeDao, TrainerDao trainerDao, TrainingDao trainingDao,
                              ProfileCredentialsGenerator credentialsGenerator) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingDao = trainingDao;
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    @Transactional
    public Trainee create(Trainee trainee) {
        ValidationUtils.validateTraineeForCreateOrUpdate(trainee);
        Credentials credentials = credentialsGenerator.generate(trainee.getFirstName(), trainee.getLastName());
        trainee.setUsername(credentials.username());
        trainee.setPassword(credentials.password());

        Trainee saved = traineeDao.save(trainee);
        LOGGER.info("Created trainee with id={}, username={}", saved.getId(), saved.getUsername());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return traineeDao.findByUsername(username)
                .filter(trainee -> trainee.getPassword().equals(password))
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> selectByUsername(String username, String password) {
        authenticateOrThrow(username, password);
        LOGGER.debug("Selecting trainee profile by username={}", username);
        return traineeDao.findByUsername(username);
    }

    @Override
    @Transactional
    public Trainee update(String username, String password, Trainee trainee) {
        Trainee existing = authenticateOrThrow(username, password);
        ValidationUtils.validateTraineeForCreateOrUpdate(trainee);

        existing.setFirstName(trainee.getFirstName());
        existing.setLastName(trainee.getLastName());
        existing.setDateOfBirth(trainee.getDateOfBirth());
        existing.setAddress(trainee.getAddress());

        Trainee updated = traineeDao.update(existing);
        LOGGER.info("Updated trainee with username={}", username);
        return updated;
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = authenticateOrThrow(username, oldPassword);
        ValidationUtils.requireText(newPassword, "New password is required");
        trainee.setPassword(newPassword);
        traineeDao.update(trainee);
        LOGGER.info("Changed trainee password for username={}", username);
    }

    @Override
    @Transactional
    public void setActive(String username, String password, boolean active) {
        Trainee trainee = authenticateOrThrow(username, password);
        ValidationUtils.requireStateChange(trainee.isActive(), active, "Trainee");
        trainee.setActive(active);
        traineeDao.update(trainee);
        LOGGER.info("Set trainee active={} for username={}", active, username);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username, String password) {
        Trainee trainee = authenticateOrThrow(username, password);
        traineeDao.delete(trainee);
        LOGGER.info("Deleted trainee profile with username={}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, String password, TraineeTrainingCriteria criteria) {
        authenticateOrThrow(username, password);
        return trainingDao.findByTraineeCriteria(username, defaultCriteria(criteria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getNotAssignedTrainers(String username, String password) {
        authenticateOrThrow(username, password);
        return trainerDao.findNotAssignedToTrainee(username);
    }

    @Override
    @Transactional
    public Trainee updateTrainers(String username, String password, List<String> trainerUsernames) {
        Trainee trainee = authenticateOrThrow(username, password);
        HashSet<Trainer> trainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            trainers.add(trainerDao.findByUsername(trainerUsername)
                    .orElseThrow(() -> new NotFoundException("Trainer not found: " + trainerUsername)));
        }
        trainee.setTrainers(trainers);
        LOGGER.info("Updated trainer list for trainee username={}", username);
        return traineeDao.update(trainee);
    }

    @Override
    @Transactional
    public Trainee update(Trainee trainee) {
        ValidationUtils.validateTraineeForCreateOrUpdate(trainee);
        Trainee updated = traineeDao.update(trainee);
        LOGGER.info("Updated trainee with id={}", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Trainee trainee = traineeDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found by id: " + id));
        traineeDao.delete(trainee);
        LOGGER.info("Deleted trainee with id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> select(Long id) {
        LOGGER.debug("Selecting trainee profile with id={}", id);
        return traineeDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> selectAll() {
        LOGGER.debug("Selecting all trainee profiles");
        return traineeDao.findAll();
    }

    private Trainee authenticateOrThrow(String username, String password) {
        return traineeDao.findByUsername(username)
                .filter(trainee -> trainee.getPassword().equals(password))
                .orElseThrow(() -> new AuthenticationException("Invalid trainee credentials"));
    }

    private TraineeTrainingCriteria defaultCriteria(TraineeTrainingCriteria criteria) {
        if (criteria == null) {
            return new TraineeTrainingCriteria(null, null, null, null);
        }
        return criteria;
    }
}
