package com.example.gymcrm.service;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.dto.TrainerProfileRequest;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.facade.GymCrmFacade;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.example.gymcrm.CsvTestData.trainer;
import static com.example.gymcrm.CsvTestData.trainerRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerServiceIntegrationTest {

    @Test
    void shouldCreateAuthenticateUpdateAndChangePassword() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            Trainer trainer = facade.createTrainer(trainerRequest("alice_yoga_trainer"));

            assertTrue(facade.authenticateTrainer(trainer.getUsername(), trainer.getPassword()));
            assertTrue(facade.selectTrainer(trainer.getUsername(), trainer.getPassword()).isPresent());

            TrainerProfileRequest update = trainerRequest("alice_strength_trainer");
            Trainer updatedTrainer = facade.updateTrainer(trainer.getUsername(), trainer.getPassword(), update);
            assertEquals("Updated", updatedTrainer.getLastName());
            assertEquals(TrainingType.STRENGTH, updatedTrainer.getSpecialization().getName());

            facade.changeTrainerPassword(trainer.getUsername(), trainer.getPassword(), "newTrainerPass");
            assertTrue(facade.authenticateTrainer(trainer.getUsername(), "newTrainerPass"));
        }
    }

    @Test
    void shouldRejectInvalidTrainerRequests() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);
            Trainer trainer = facade.createTrainer(trainerRequest("victor_strength_trainer"));

            assertThrows(AuthenticationException.class,
                    () -> facade.selectTrainer(trainer.getUsername(), "wrong-password"));
            assertThrows(IllegalStateException.class,
                    () -> facade.setTrainerActive(trainer.getUsername(), trainer.getPassword(), true));
            facade.setTrainerActive(trainer.getUsername(), trainer.getPassword(), false);
            facade.setTrainerActive(trainer.getUsername(), trainer.getPassword(), true);

            assertThrows(IllegalArgumentException.class,
                    () -> facade.createTrainer(new TrainerProfileRequest(null, "Broken", TrainingType.YOGA)));
        }
    }

    @Test
    void shouldExposeLegacySelectionAndUpdateMethods() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            Trainer trainer = facade.createTrainer(trainer("greg_cardio_trainer"));

            assertEquals(1, facade.selectAllTrainers().size());
            assertTrue(facade.selectTrainer(trainer.getId()).isPresent());
            assertFalse(facade.authenticateTrainer(trainer.getUsername(), "bad-password"));

            trainer.setLastName("Legacy");

            assertEquals("Legacy", facade.updateTrainer(trainer).getLastName());
        }
    }
}
