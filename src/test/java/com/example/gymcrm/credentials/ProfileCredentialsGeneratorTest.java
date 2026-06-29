package com.example.gymcrm.credentials;

import com.example.gymcrm.dao.impl.InMemoryTraineeDao;
import com.example.gymcrm.dao.impl.InMemoryTrainerDao;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ProfileCredentialsGeneratorTest {
    private InMemoryTraineeDao traineeDao;
    private InMemoryTrainerDao trainerDao;
    private ProfileCredentialsGenerator generator;

    @BeforeEach
    void setUp() {
        traineeDao= new InMemoryTraineeDao();
        traineeDao.setStorage(new LinkedHashMap<>());

        trainerDao = new InMemoryTrainerDao();
        trainerDao.setStorage(new LinkedHashMap<>());

        generator = new ProfileCredentialsGenerator();
        generator.setTraineeDao(traineeDao);
        generator.setTrainerDao(trainerDao);
        generator.setPasswordGenerator(new PasswordGenerator());
    }

    @Test
    void shouldGenerateBaseUsernameWhenNoDuplicateExists() {
        Credentials credentials = generator.generate("John", "Smith");

        assertEquals("John.Smith", credentials.username());
        assertEquals(10, credentials.password().length());
    }

    @Test
    void shouldGenerateUsernameWithSuffixWhenDuplicateExists() {
        Trainee existing = new Trainee();
        existing.setFirstName("John");
        existing.setLastName("Smith");
        existing.setUsername("John.Smith");

        traineeDao.save(existing);

        Credentials credentials = generator.generate("John", "Smith");

        assertEquals("John.Smith1", credentials.username());
        assertEquals(10, credentials.password().length());
    }

    @Test
    void shouldCheckDuplicatesAcrossTraineesAndTrainers() {
        Trainee trainee = new Trainee();
        trainee.setUsername("John.Smith");
        traineeDao.save(trainee);

        Trainer trainer = new Trainer();
        trainer.setUsername("John.Smith1");
        trainerDao.save(trainer);

        Credentials credentials = generator.generate("John", "Smith");

        assertEquals("John.Smith2", credentials.username());
    }
}