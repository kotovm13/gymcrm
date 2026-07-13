package com.example.gymcrm.service.impl;

import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;
    private final TrainingTypeDao trainingTypeDao;
    private final ProfileCredentialsGenerator credentialsGenerator;

    public TrainerServiceImpl(TrainerDao trainerDao, TrainingDao trainingDao, TrainingTypeDao trainingTypeDao,
                              ProfileCredentialsGenerator credentialsGenerator) {
        this.trainerDao = trainerDao;
        this.trainingDao = trainingDao;
        this.trainingTypeDao = trainingTypeDao;
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    @Transactional
    public Trainer create(Trainer trainer) {
        ValidationUtils.validateTrainerForCreateOrUpdate(trainer);
        trainer.setSpecialization(resolveTrainingType(trainer.getSpecialization().getName()));
        Credentials credentials = credentialsGenerator.generate(trainer.getFirstName(), trainer.getLastName());
        trainer.setUsername(credentials.username());
        trainer.setPassword(credentials.password());

        Trainer saved = trainerDao.save(trainer);
        LOGGER.info("Created trainer with id={}, username={}", saved.getId(), saved.getUsername());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return trainerDao.findByUsername(username)
                .filter(trainer -> trainer.getPassword().equals(password))
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> selectByUsername(String username, String password) {
        authenticateOrThrow(username, password);
        LOGGER.debug("Selecting trainer profile by username={}", username);
        return trainerDao.findByUsername(username);
    }

    @Override
    @Transactional
    public Trainer update(String username, String password, Trainer trainer) {
        Trainer existing = authenticateOrThrow(username, password);
        ValidationUtils.validateTrainerForCreateOrUpdate(trainer);

        existing.setFirstName(trainer.getFirstName());
        existing.setLastName(trainer.getLastName());
        existing.setSpecialization(resolveTrainingType(trainer.getSpecialization().getName()));

        Trainer updated = trainerDao.update(existing);
        LOGGER.info("Updated trainer with username={}", username);
        return updated;
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = authenticateOrThrow(username, oldPassword);
        ValidationUtils.requireText(newPassword, "New password is required");
        trainer.setPassword(newPassword);
        trainerDao.update(trainer);
        LOGGER.info("Changed trainer password for username={}", username);
    }

    @Override
    @Transactional
    public void setActive(String username, String password, boolean active) {
        Trainer trainer = authenticateOrThrow(username, password);
        ValidationUtils.requireStateChange(trainer.isActive(), active, "Trainer");
        trainer.setActive(active);
        trainerDao.update(trainer);
        LOGGER.info("Set trainer active={} for username={}", active, username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, String password, TrainerTrainingCriteria criteria) {
        authenticateOrThrow(username, password);
        return trainingDao.findByTrainerCriteria(username, defaultCriteria(criteria));
    }

    @Override
    @Transactional
    public Trainer update(Trainer trainer) {
        ValidationUtils.validateTrainerForCreateOrUpdate(trainer);
        trainer.setSpecialization(resolveTrainingType(trainer.getSpecialization().getName()));
        Trainer updated = trainerDao.update(trainer);
        LOGGER.info("Updated trainer with id={}", updated.getId());
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> select(Long id) {
        LOGGER.debug("Selecting trainer with id={}", id);
        return trainerDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> selectAll() {
        LOGGER.debug("Selecting all trainer profiles");
        return trainerDao.findAll();
    }

    private Trainer authenticateOrThrow(String username, String password) {
        return trainerDao.findByUsername(username)
                .filter(trainer -> trainer.getPassword().equals(password))
                .orElseThrow(() -> new AuthenticationException("Invalid trainer credentials"));
    }

    private TrainingType resolveTrainingType(String name) {
        return trainingTypeDao.findByName(name)
                .orElseThrow(() -> new NotFoundException("Training type not found: " + name));
    }

    private TrainerTrainingCriteria defaultCriteria(TrainerTrainingCriteria criteria) {
        if (criteria == null) {
            return new TrainerTrainingCriteria(null, null, null);
        }
        return criteria;
    }
}
