package com.example.gymcrm.service.impl;

import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.service.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeServiceImpl.class);

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> selectAll() {
        LOGGER.debug("Selecting all training types");
        return trainingTypeRepository.findAll();
    }
}
