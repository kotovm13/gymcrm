package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private TraineeDao traineeDao;
    private ProfileCredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setCredentialsGenerator(ProfileCredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    public Trainee create(Trainee trainee) {

        Credentials credentials = credentialsGenerator.generate(
                trainee.getFirstName(),
                trainee.getLastName()
        );

        trainee.setUsername(credentials.username());
        trainee.setPassword(credentials.password());

        Trainee saved = traineeDao.save(trainee);
        LOGGER.info("Created trainee with id={}, username={}", saved.getId(), saved.getUsername());

        return saved;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee updated = traineeDao.update(trainee);
        LOGGER.info("Updated trainee with id={}", updated.getId());

        return updated;
    }

    @Override
    public void delete(Long id) {
        traineeDao.deleteById(id);
        LOGGER.info("Deleted trainee with id={}", id);
    }

    @Override
    public Optional<Trainee> select(Long id) {
        LOGGER.debug("Selecting trainee profile with id={}", id);

        return traineeDao.findById(id);
    }

    @Override
    public List<Trainee> selectAll() {
        LOGGER.debug("Selecting all trainee profiles");

        return traineeDao.findAll();
    }
}
