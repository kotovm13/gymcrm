package com.example.gymcrm.service.impl;

import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.credentials.PasswordHasher;
import com.example.gymcrm.credentials.ProfileRegistration;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.TraineeProfileRequest;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.TraineeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class TraineeServiceImpl implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileCredentialsGenerator credentialsGenerator;
    private final PasswordHasher passwordHasher;

    public TraineeServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository, TrainingRepository trainingRepository,
                              ProfileCredentialsGenerator credentialsGenerator, PasswordHasher passwordHasher) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.credentialsGenerator = credentialsGenerator;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public ProfileRegistration<Trainee> create(TraineeProfileRequest request) {
        Trainee trainee = new Trainee();
        applyProfile(request, trainee);

        Credentials credentials = credentialsGenerator.generate(request.firstName(), request.lastName());
        trainee.setUsername(credentials.username());
        trainee.setPassword(passwordHasher.hash(credentials.password()));

        Trainee saved = traineeRepository.save(trainee);
        LOGGER.info("Created trainee with id={}, username={}", saved.getId(), saved.getUsername());
        return new ProfileRegistration<>(saved, credentials.password());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return traineeRepository.findByUsername(username)
                .filter(trainee -> passwordHasher.matches(password, trainee.getPassword()))
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> selectByUsername(String username, String password) {
        authenticateOrThrow(username, password);
        LOGGER.debug("Selecting trainee profile by username={}", username);
        return traineeRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Trainee update(String username, String password, TraineeProfileRequest request) {
        Trainee existing = authenticateOrThrow(username, password);
        applyProfile(request, existing);

        Trainee updated = traineeRepository.update(existing);
        LOGGER.info("Updated trainee with username={}", username);
        return updated;
    }

    @Override
    @Transactional
    public Trainee updateProfile(String username, String password, TraineeProfileRequest request, boolean active) {
        Trainee existing = authenticateOrThrow(username, password);
        applyProfile(request, existing);
        existing.setActive(active);
        Trainee updated = traineeRepository.update(existing);
        LOGGER.info("Updated trainee profile with username={}, active={}", username, active);
        return updated;
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = authenticateOrThrow(username, oldPassword);
        requireText(newPassword, "New password is required");
        trainee.setPassword(passwordHasher.hash(newPassword));
        traineeRepository.update(trainee);
        LOGGER.info("Changed trainee password for username={}", username);
    }

    @Override
    @Transactional
    public void setActive(String username, String password, boolean active) {
        Trainee trainee = authenticateOrThrow(username, password);
        requireStateChange(trainee.isActive(), active, "Trainee");
        trainee.setActive(active);
        traineeRepository.update(trainee);
        LOGGER.info("Set trainee active={} for username={}", active, username);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username, String password) {
        Trainee trainee = authenticateOrThrow(username, password);
        traineeRepository.delete(trainee);
        LOGGER.info("Deleted trainee profile with username={}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, String password, TraineeTrainingCriteria criteria) {
        authenticateOrThrow(username, password);
        return trainingRepository.findByTraineeCriteria(username, defaultCriteria(criteria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getNotAssignedTrainers(String username, String password) {
        authenticateOrThrow(username, password);
        return trainerRepository.findNotAssignedToTrainee(username);
    }

    @Override
    @Transactional
    public Trainee updateTrainers(String username, String password, List<String> trainerUsernames) {
        Trainee trainee = authenticateOrThrow(username, password);
        HashSet<Trainer> trainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            trainers.add(trainerRepository.findByUsername(trainerUsername)
                    .orElseThrow(() -> new NotFoundException("Trainer not found: " + trainerUsername)));
        }
        trainee.setTrainers(trainers);
        LOGGER.info("Updated trainer list for trainee username={}", username);
        return traineeRepository.update(trainee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found by id: " + id));
        traineeRepository.delete(trainee);
        LOGGER.info("Deleted trainee with id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> select(Long id) {
        LOGGER.debug("Selecting trainee profile with id={}", id);
        return traineeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> selectAll() {
        LOGGER.debug("Selecting all trainee profiles");
        return traineeRepository.findAll();
    }

    private Trainee authenticateOrThrow(String username, String password) {
        return traineeRepository.findByUsername(username)
                .filter(trainee -> passwordHasher.matches(password, trainee.getPassword()))
                .orElseThrow(() -> new AuthenticationException("Invalid trainee credentials"));
    }

    private TraineeTrainingCriteria defaultCriteria(TraineeTrainingCriteria criteria) {
        if (criteria == null) {
            return new TraineeTrainingCriteria(null, null, null, null);
        }
        return criteria;
    }

    private void applyProfile(TraineeProfileRequest request, Trainee trainee) {
        trainee.setFirstName(request.firstName());
        trainee.setLastName(request.lastName());
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireStateChange(boolean currentState, boolean newState, String profileType) {
        if (currentState == newState) {
            throw new IllegalStateException(profileType + " active state is already " + newState);
        }
    }
}
