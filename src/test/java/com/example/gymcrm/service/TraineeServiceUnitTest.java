package com.example.gymcrm.service;

import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.repository.TraineeRepository;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.gymcrm.CsvTestData.trainee;
import static com.example.gymcrm.CsvTestData.trainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceUnitTest {
    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private ProfileCredentialsGenerator credentialsGenerator;

    @Test
    void shouldUpdateTraineeTrainerList() {
        TraineeServiceImpl service = new TraineeServiceImpl(
                traineeRepository,
                trainerRepository,
                trainingRepository,
                credentialsGenerator
        );
        Trainee trainee = trainee("kate_trainee");
        Trainer trainer = trainer("sara_yoga_trainer");
        when(traineeRepository.findByUsername("Kate.Moss")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Sara.Hill")).thenReturn(Optional.of(trainer));
        when(traineeRepository.update(trainee)).thenReturn(trainee);

        Trainee updated = service.updateTrainers("Kate.Moss", "password", List.of("Sara.Hill"));

        assertEquals(1, updated.getTrainers().size());
        assertEquals(trainer, updated.getTrainers().iterator().next());
        verify(traineeRepository).update(trainee);
    }

    @Test
    void shouldRejectMissingTrainerWhenUpdatingTrainerList() {
        TraineeServiceImpl service = new TraineeServiceImpl(
                traineeRepository,
                trainerRepository,
                trainingRepository,
                credentialsGenerator
        );
        Trainee trainee = trainee("kate_trainee");
        when(traineeRepository.findByUsername("Kate.Moss")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.updateTrainers("Kate.Moss", "password", List.of("missing")));
    }
}
