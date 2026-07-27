package com.example.gymcrm.service.impl;

import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.credentials.PasswordHasher;
import com.example.gymcrm.credentials.ProfileRegistration;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.dto.TrainerProfileRequest;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final ProfileCredentialsGenerator credentialsGenerator;
    private final PasswordHasher passwordHasher;

    public TrainerServiceImpl(TrainerRepository trainerRepository, TrainingRepository trainingRepository, TrainingTypeRepository trainingTypeRepository,
                              ProfileCredentialsGenerator credentialsGenerator, PasswordHasher passwordHasher) {
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.credentialsGenerator = credentialsGenerator;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public ProfileRegistration<Trainer> create(TrainerProfileRequest request) {
        Trainer trainer = new Trainer();
        applyProfile(request, trainer);

        Credentials credentials = credentialsGenerator.generate(request.firstName(), request.lastName());
        trainer.setUsername(credentials.username());
        trainer.setPassword(passwordHasher.hash(credentials.password()));

        Trainer saved = trainerRepository.save(trainer);
        LOGGER.info("Created trainer with id={}, username={}", saved.getId(), saved.getUsername());
        return new ProfileRegistration<>(saved, credentials.password());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return trainerRepository.findByUsername(username)
                .filter(trainer -> passwordHasher.matches(password, trainer.getPassword()))
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> selectByUsername(String username, String password) {
        authenticateOrThrow(username, password);
        LOGGER.debug("Selecting trainer profile by username={}", username);
        return trainerRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Trainer update(String username, String password, TrainerProfileRequest request) {
        Trainer existing = authenticateOrThrow(username, password);
        applyProfile(request, existing);

        Trainer updated = trainerRepository.update(existing);
        LOGGER.info("Updated trainer with username={}", username);
        return updated;
    }

    @Override
    @Transactional
    public Trainer updateProfile(String username, String password, TrainerProfileRequest request, boolean active) {
        Trainer existing = authenticateOrThrow(username, password);
        applyProfile(request, existing);
        existing.setActive(active);
        Trainer updated = trainerRepository.update(existing);
        LOGGER.info("Updated trainer profile with username={}, active={}", username, active);
        return updated;
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = authenticateOrThrow(username, oldPassword);
        requireText(newPassword, "New password is required");
        trainer.setPassword(passwordHasher.hash(newPassword));
        trainerRepository.update(trainer);
        LOGGER.info("Changed trainer password for username={}", username);
    }

    @Override
    @Transactional
    public void setActive(String username, String password, boolean active) {
        Trainer trainer = authenticateOrThrow(username, password);
        requireStateChange(trainer.isActive(), active, "Trainer");
        trainer.setActive(active);
        trainerRepository.update(trainer);
        LOGGER.info("Set trainer active={} for username={}", active, username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, String password, TrainerTrainingCriteria criteria) {
        authenticateOrThrow(username, password);
        return trainingRepository.findByTrainerCriteria(username, defaultCriteria(criteria));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> select(Long id) {
        LOGGER.debug("Selecting trainer with id={}", id);
        return trainerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> selectAll() {
        LOGGER.debug("Selecting all trainer profiles");
        return trainerRepository.findAll();
    }

    private Trainer authenticateOrThrow(String username, String password) {
        return trainerRepository.findByUsername(username)
                .filter(trainer -> passwordHasher.matches(password, trainer.getPassword()))
                .orElseThrow(() -> new AuthenticationException("Invalid trainer credentials"));
    }

    private TrainingType resolveTrainingType(String name) {
        return trainingTypeRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Training type not found: " + name));
    }

    private TrainerTrainingCriteria defaultCriteria(TrainerTrainingCriteria criteria) {
        if (criteria == null) {
            return new TrainerTrainingCriteria(null, null, null);
        }
        return criteria;
    }

    private void applyProfile(TrainerProfileRequest request, Trainer trainer) {
        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        trainer.setSpecialization(resolveTrainingType(request.specialization()));
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
