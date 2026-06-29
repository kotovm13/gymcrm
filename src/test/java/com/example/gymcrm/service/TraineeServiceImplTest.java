package com.example.gymcrm.service;

import com.example.gymcrm.credentials.PasswordGenerator;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.dao.impl.InMemoryTraineeDao;
import com.example.gymcrm.dao.impl.InMemoryTrainerDao;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeServiceImplTest {
    private InMemoryTraineeDao traineeDao;
    private InMemoryTrainerDao trainerDao;
    private TraineeServiceImpl service;

    @BeforeEach
    void setUp() {
        traineeDao = new InMemoryTraineeDao();
        traineeDao.setStorage(new LinkedHashMap<>());

        trainerDao = new InMemoryTrainerDao();
        trainerDao.setStorage(new LinkedHashMap<>());

        ProfileCredentialsGenerator credentialsGenerator = new ProfileCredentialsGenerator();
        credentialsGenerator.setTraineeDao(traineeDao);
        credentialsGenerator.setTrainerDao(trainerDao);
        credentialsGenerator.setPasswordGenerator(new PasswordGenerator());

        service = new TraineeServiceImpl();
        service.setTraineeDao(traineeDao);
        service.setCredentialsGenerator(credentialsGenerator);
    }

    @Test
    void shouldCreateTraineeWithGeneratedCredentials() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Emma");
        trainee.setLastName("Stone");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(1995, 2, 3));
        trainee.setAddress("Main Street");

        Trainee created = service.create(trainee);

        assertEquals(1L, created.getId());
        assertEquals("Emma.Stone", created.getUsername());
        assertNotNull(created.getPassword());
        assertEquals(10, created.getPassword().length());
        assertTrue(service.select(created.getId()).isPresent());
    }

    @Test
    void shouldUpdateAndDeleteTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Emma");
        trainee.setLastName("Stone");
        trainee.setAddress("Old Address");

        Trainee created = service.create(trainee);
        created.setAddress("New Address");

        service.update(created);

        assertEquals("New Address", service.select(created.getId()).orElseThrow().getAddress());

        service.delete(created.getId());

        assertTrue(service.select(created.getId()).isEmpty());
    }
}
