package com.example.gymcrm.service;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.facade.GymCrmFacade;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static com.example.gymcrm.CsvTestData.traineeRequest;
import static com.example.gymcrm.CsvTestData.trainerRequest;
import static com.example.gymcrm.CsvTestData.training;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrainingServiceIntegrationTest {

    @Test
    void shouldAddTrainingAndFilterTrainingLists() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);
            Trainee trainee = facade.createTrainee(traineeRequest("mark_trainee"));
            Trainer trainer = facade.createTrainer(trainerRequest("sara_yoga_trainer"));
            var trainingData = training("morning_yoga");

            Training training = facade.addTraining(new AddTrainingRequest(
                    trainer.getUsername(),
                    trainer.getPassword(),
                    trainee.getUsername(),
                    trainingData.trainingName(),
                    trainingData.trainingType(),
                    trainingData.trainingDate(),
                    trainingData.durationMinutes()
            ));

            assertNotNull(training.getId());
            assertEquals(1, facade.selectAllTrainings().size());

            List<Training> traineeTrainings = facade.getTraineeTrainings(
                    trainee.getUsername(),
                    trainee.getPassword(),
                    new TraineeTrainingCriteria(
                            trainingData.fromDate(),
                            trainingData.toDate(),
                            trainingData.trainerNameFilter(),
                            trainingData.trainingType()
                    )
            );
            assertEquals(1, traineeTrainings.size());

            List<Training> trainerTrainings = facade.getTrainerTrainings(
                    trainer.getUsername(),
                    trainer.getPassword(),
                    new TrainerTrainingCriteria(
                            trainingData.fromDate(),
                            trainingData.toDate(),
                            trainingData.traineeNameFilter()
                    )
            );
            assertEquals(1, trainerTrainings.size());
            assertEquals(1, facade.getTraineeTrainings(
                    trainee.getUsername(),
                    trainee.getPassword(),
                    null
            ).size());
            assertEquals(1, facade.getTrainerTrainings(
                    trainer.getUsername(),
                    trainer.getPassword(),
                    null
            ).size());
        }
    }

    @Test
    void shouldRejectUnknownTrainingType() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);
            Trainee trainee = facade.createTrainee(traineeRequest("nina_trainee"));
            Trainer trainer = facade.createTrainer(trainerRequest("victor_strength_trainer"));
            var unknownTypeTraining = training("unknown_type");

            assertThrows(NotFoundException.class,
                    () -> facade.addTraining(new AddTrainingRequest(
                            trainer.getUsername(),
                            trainer.getPassword(),
                            trainee.getUsername(),
                            unknownTypeTraining.trainingName(),
                            unknownTypeTraining.trainingType(),
                            unknownTypeTraining.trainingDate(),
                            unknownTypeTraining.durationMinutes()
                    )));
        }
    }
}
