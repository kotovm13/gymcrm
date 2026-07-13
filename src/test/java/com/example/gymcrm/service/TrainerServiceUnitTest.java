package com.example.gymcrm.service;

import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.gymcrm.CsvTestData.profile;
import static com.example.gymcrm.CsvTestData.trainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceUnitTest {
    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private ProfileCredentialsGenerator credentialsGenerator;

    @Test
    void shouldCreateTrainerWithResolvedTrainingTypeAndGeneratedCredentials() {
        TrainerServiceImpl service = new TrainerServiceImpl(
                trainerDao,
                trainingDao,
                trainingTypeDao,
                credentialsGenerator
        );
        TrainingType yoga = new TrainingType(1L, TrainingType.YOGA);
        Trainer trainer = trainer("alice_yoga_trainer");
        var profile = profile("alice_yoga_trainer");
        when(trainingTypeDao.findByName(TrainingType.YOGA)).thenReturn(Optional.of(yoga));
        when(credentialsGenerator.generate(profile.firstName(), profile.lastName()))
                .thenReturn(new Credentials(profile.username(), profile.password()));
        when(trainerDao.save(trainer)).thenReturn(trainer);

        Trainer saved = service.create(trainer);

        assertEquals("Alice.Brown", saved.getUsername());
        assertEquals("password", saved.getPassword());
        assertEquals(yoga, saved.getSpecialization());
        verify(trainerDao).save(trainer);
    }

    @Test
    void shouldRejectUnknownTrainingTypeOnCreate() {
        TrainerServiceImpl service = new TrainerServiceImpl(
                trainerDao,
                trainingDao,
                trainingTypeDao,
                credentialsGenerator
        );
        Trainer trainer = trainer("alice_yoga_trainer");
        trainer.setSpecialization(new TrainingType(null, "BOXING"));
        when(trainingTypeDao.findByName("BOXING")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(trainer));
    }
}
