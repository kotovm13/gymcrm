package com.example.gymcrm.service;

import com.example.gymcrm.dao.impl.InMemoryTrainingDao;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingServiceImplTest {
    private TrainingServiceImpl service;

    @BeforeEach
    void setUp() {
        InMemoryTrainingDao trainingDao = new InMemoryTrainingDao();
        trainingDao.setStorage(new LinkedHashMap<>());

        service = new TrainingServiceImpl();
        service.setTrainingDao(trainingDao);
    }

    @Test
    void shouldCreateAndSelectTraining() {
        Training training = new Training(
                null,
                1L,
                1L,
                "Morning Yoga",
                TrainingType.YOGA,
                LocalDate.of(2026, 6, 25),
                60
        );

        Training created = service.create(training);

        assertEquals(1L, created.getId());
        assertTrue(service.select(created.getId()).isPresent());
        assertEquals(1, service.selectAll().size());
    }
}
