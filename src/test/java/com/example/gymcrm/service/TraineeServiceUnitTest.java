package com.example.gymcrm.service;

import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.TrainingDao;
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
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private ProfileCredentialsGenerator credentialsGenerator;

    @Test
    void shouldUpdateTraineeTrainerList() {
        TraineeServiceImpl service = new TraineeServiceImpl(
                traineeDao,
                trainerDao,
                trainingDao,
                credentialsGenerator
        );
        Trainee trainee = trainee("kate_trainee");
        Trainer trainer = trainer("sara_yoga_trainer");
        when(traineeDao.findByUsername("Kate.Moss")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("Sara.Hill")).thenReturn(Optional.of(trainer));
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee updated = service.updateTrainers("Kate.Moss", "password", List.of("Sara.Hill"));

        assertEquals(1, updated.getTrainers().size());
        assertEquals(trainer, updated.getTrainers().iterator().next());
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldRejectMissingTrainerWhenUpdatingTrainerList() {
        TraineeServiceImpl service = new TraineeServiceImpl(
                traineeDao,
                trainerDao,
                trainingDao,
                credentialsGenerator
        );
        Trainee trainee = trainee("kate_trainee");
        when(traineeDao.findByUsername("Kate.Moss")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.updateTrainers("Kate.Moss", "password", List.of("missing")));
    }
}
