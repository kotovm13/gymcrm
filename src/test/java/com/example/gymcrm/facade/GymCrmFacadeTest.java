package com.example.gymcrm.facade;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GymCrmFacadeTest {

    @Test
    void shouldDelegateOperationsToServices() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            Trainee trainee = new Trainee();
            trainee.setFirstName("Mark");
            trainee.setLastName("Lee");
            trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
            trainee.setAddress("Test Street");

            Trainee createdTrainee = facade.createTrainee(trainee);
            assertTrue(facade.selectTrainee(createdTrainee.getId()).isPresent());
            assertFalse(facade.selectAllTrainees().isEmpty());

            createdTrainee.setAddress("New Street");
            assertEquals("New Street", facade.updateTrainee(createdTrainee).getAddress());

            Trainer trainer = new Trainer();
            trainer.setFirstName("Sara");
            trainer.setLastName("Hill");
            trainer.setSpecialization(TrainingType.CARDIO);

            Trainer createdTrainer = facade.createTrainer(trainer);
            assertTrue(facade.selectTrainer(createdTrainer.getId()).isPresent());
            assertFalse(facade.selectAllTrainers().isEmpty());

            createdTrainer.setSpecialization(TrainingType.YOGA);
            assertEquals(TrainingType.YOGA, facade.updateTrainer(createdTrainer).getSpecialization());

            Training training = new Training(
                    null,
                    createdTrainee.getId(),
                    createdTrainer.getId(),
                    "Cardio Session",
                    TrainingType.CARDIO,
                    LocalDate.of(2026, 6, 29),
                    45
            );

            Training createdTraining = facade.createTraining(training);
            assertTrue(facade.selectTraining(createdTraining.getId()).isPresent());
            assertFalse(facade.selectAllTrainings().isEmpty());

            facade.deleteTrainee(createdTrainee.getId());
            assertTrue(facade.selectTrainee(createdTrainee.getId()).isEmpty());
        }
    }
}