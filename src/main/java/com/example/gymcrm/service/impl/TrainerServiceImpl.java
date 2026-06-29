package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private TrainerDao trainerDao;
    private ProfileCredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setCredentialsGenerator(ProfileCredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        Credentials credentials = credentialsGenerator.generate(
                trainer.getFirstName(),
                trainer.getLastName()
        );

        trainer.setUsername(credentials.username());
        trainer.setPassword(credentials.password());

        Trainer saved = trainerDao.save(trainer);
        LOGGER.info("Created trainer with id={}, username={}", saved.getId(), saved.getUsername());

        return saved;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Trainer updated = trainerDao.update(trainer);
        LOGGER.info("Updated trainer with id={}", updated.getId());

        return updated;
    }

    @Override
    public Optional<Trainer> select(Long id) {
        LOGGER.debug("Selecting trainer with id={}", id);

        return trainerDao.findById(id);
    }

    @Override
    public List<Trainer> selectAll() {
        LOGGER.debug("Selecting all trainer profiles");

        return trainerDao.findAll();
    }
}
