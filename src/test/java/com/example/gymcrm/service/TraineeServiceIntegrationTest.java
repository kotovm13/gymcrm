package com.example.gymcrm.service;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.dto.TraineeProfileRequest;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.facade.GymCrmFacade;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static com.example.gymcrm.CsvTestData.profile;
import static com.example.gymcrm.CsvTestData.traineeRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeServiceIntegrationTest {

    @Test
    void shouldCreateAuthenticateUpdateAndChangePassword() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            Trainee trainee = facade.createTrainee(traineeRequest("john_trainee"));
            var updateData = profile("john_updated_trainee");

            assertNotNull(trainee.getUsername());
            assertNotNull(trainee.getPassword());
            assertNotEquals(trainee.getPassword(), facade.selectTrainee(trainee.getId()).orElseThrow().getPassword());
            assertTrue(facade.authenticateTrainee(trainee.getUsername(), trainee.getPassword()));
            assertTrue(facade.selectTrainee(trainee.getUsername(), trainee.getPassword()).isPresent());

            TraineeProfileRequest update = new TraineeProfileRequest(
                    updateData.firstName(),
                    updateData.lastName(),
                    updateData.dateOfBirth(),
                    updateData.address()
            );
            Trainee updated = facade.updateTrainee(trainee.getUsername(), trainee.getPassword(), update);
            assertEquals("Updated", updated.getLastName());
            assertEquals("New Address", updated.getAddress());

            facade.changeTraineePassword(trainee.getUsername(), trainee.getPassword(), "newTraineePass");
            assertTrue(facade.authenticateTrainee(trainee.getUsername(), "newTraineePass"));
            assertThrows(AuthenticationException.class,
                    () -> facade.selectTrainee(trainee.getUsername(), trainee.getPassword()));
        }
    }

    @Test
    void shouldRejectInvalidTraineeRequests() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);
            Trainee trainee = facade.createTrainee(traineeRequest("nina_trainee"));

            assertThrows(IllegalStateException.class,
                    () -> facade.setTraineeActive(trainee.getUsername(), trainee.getPassword(), true));
            facade.setTraineeActive(trainee.getUsername(), trainee.getPassword(), false);
            facade.setTraineeActive(trainee.getUsername(), trainee.getPassword(), true);

            assertThrows(IllegalArgumentException.class,
                    () -> facade.changeTraineePassword(trainee.getUsername(), trainee.getPassword(), " "));
            assertThrows(NotFoundException.class,
                    () -> facade.updateTraineeTrainers(trainee.getUsername(), trainee.getPassword(), List.of("missing")));
            assertThrows(ConstraintViolationException.class, () -> facade.createTrainee(traineeRequest("broken_trainee")));
            assertThrows(NotFoundException.class, () -> facade.deleteTrainee(999L));
        }
    }

    @Test
    void shouldExposeSelectionMethods() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            Trainee trainee = facade.createTrainee(traineeRequest("kate_trainee"));

            assertEquals(1, facade.selectAllTrainees().size());
            assertTrue(facade.selectTrainee(trainee.getId()).isPresent());
            assertFalse(facade.authenticateTrainee(trainee.getUsername(), "bad-password"));
        }
    }
}
