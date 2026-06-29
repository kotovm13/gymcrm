package com.example.gymcrm.service;

import com.example.gymcrm.credentials.PasswordGenerator;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.dao.impl.InMemoryTraineeDao;
import com.example.gymcrm.dao.impl.InMemoryTrainerDao;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerServiceImplTest {
    private TrainerServiceImpl service;

    @BeforeEach
    void setUp() {
        InMemoryTraineeDao traineeDao = new InMemoryTraineeDao();
        traineeDao.setStorage(new LinkedHashMap<>());

        InMemoryTrainerDao trainerDao = new InMemoryTrainerDao();
        trainerDao.setStorage(new LinkedHashMap<>());

        ProfileCredentialsGenerator credentialsGenerator = new ProfileCredentialsGenerator();
        credentialsGenerator.setTraineeDao(traineeDao);
        credentialsGenerator.setTrainerDao(trainerDao);
        credentialsGenerator.setPasswordGenerator(new PasswordGenerator());

        service = new TrainerServiceImpl();
        service.setTrainerDao(trainerDao);
        service.setCredentialsGenerator(credentialsGenerator);
    }

    @Test
    void shouldCreateTrainerWithGeneratedCredentials() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Alice");
        trainer.setLastName("Brown");
        trainer.setSpecialization(TrainingType.YOGA);

        Trainer created = service.create(trainer);

        assertEquals(1L, created.getId());
        assertEquals("Alice.Brown", created.getUsername());
        assertNotNull(created.getPassword());
        assertEquals(10, created.getPassword().length());
        assertTrue(service.select(created.getId()).isPresent());
    }

    @Test
    void shouldUpdateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Alice");
        trainer.setLastName("Brown");
        trainer.setSpecialization(TrainingType.YOGA);

        Trainer created = service.create(trainer);
        created.setSpecialization(TrainingType.STRENGTH);

        service.update(created);

        assertEquals(TrainingType.STRENGTH, service.select(created.getId()).orElseThrow().getSpecialization());
        assertEquals(1, service.selectAll().size());
    }
}
