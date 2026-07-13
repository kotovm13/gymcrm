package com.example.gymcrm;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.facade.GymCrmFacade;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static com.example.gymcrm.CsvTestData.traineeRequest;
import static com.example.gymcrm.CsvTestData.trainerRequest;
import static com.example.gymcrm.CsvTestData.training;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HibernateTaskIntegrationTest {

    @Test
    void shouldManageTrainerAssignmentsAndCascadeDeleteTraineeTrainings() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            Trainee trainee = facade.createTrainee(traineeRequest("mark_trainee"));
            Trainer yogaTrainer = facade.createTrainer(trainerRequest("sara_yoga_trainer"));
            Trainer strengthTrainer = facade.createTrainer(trainerRequest("bob_strength_trainer"));
            var training = training("morning_yoga");

            facade.updateTraineeTrainers(
                    trainee.getUsername(),
                    trainee.getPassword(),
                    List.of(yogaTrainer.getUsername())
            );

            List<Trainer> notAssigned = facade.getNotAssignedTrainers(trainee.getUsername(), trainee.getPassword());
            assertTrue(notAssigned.stream().anyMatch(trainer -> trainer.getUsername().equals(strengthTrainer.getUsername())));
            assertFalse(notAssigned.stream().anyMatch(trainer -> trainer.getUsername().equals(yogaTrainer.getUsername())));

            facade.addTraining(new AddTrainingRequest(
                    yogaTrainer.getUsername(),
                    yogaTrainer.getPassword(),
                    trainee.getUsername(),
                    training.trainingName(),
                    training.trainingType(),
                    training.trainingDate(),
                    training.durationMinutes()
            ));

            facade.deleteTrainee(trainee.getUsername(), trainee.getPassword());

            assertTrue(facade.selectTrainee(trainee.getId()).isEmpty());
            assertTrue(facade.selectAllTrainings().isEmpty());
        }
    }
}
