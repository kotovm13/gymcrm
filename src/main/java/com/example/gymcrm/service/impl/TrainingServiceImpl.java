package com.example.gymcrm.service.impl;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }


    @Override
    public Training create(Training training) {
        Training saved = trainingDao.save(training);
        LOGGER.info("Created training with id={}", training.getId());

        return saved;
    }

    @Override
    public Optional<Training> select(Long id) {
        LOGGER.debug("Selecting training with id={}", id);

        return trainingDao.findById(id);
    }

    @Override
    public List<Training> selectAll() {
        LOGGER.debug("Selecting all trainings");

        return trainingDao.findAll();
    }
}
