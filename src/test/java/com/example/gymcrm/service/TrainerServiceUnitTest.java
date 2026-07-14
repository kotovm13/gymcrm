package com.example.gymcrm.service;

import com.example.gymcrm.credentials.Credentials;
import com.example.gymcrm.credentials.ProfileCredentialsGenerator;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.dto.TrainerProfileRequest;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.gymcrm.CsvTestData.profile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceUnitTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private ProfileCredentialsGenerator credentialsGenerator;

    private TrainerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TrainerServiceImpl(
                trainerRepository,
                trainingRepository,
                trainingTypeRepository,
                credentialsGenerator
        );
    }

    @Test
    void shouldCreateTrainerWithResolvedTrainingTypeAndGeneratedCredentials() {
        TrainingType yoga = new TrainingType(1L, TrainingType.YOGA);
        var profile = profile("alice_yoga_trainer");
        TrainerProfileRequest request = new TrainerProfileRequest(
                profile.firstName(),
                profile.lastName(),
                profile.trainingType()
        );
        when(trainingTypeRepository.findByName(TrainingType.YOGA)).thenReturn(Optional.of(yoga));
        when(credentialsGenerator.generate(profile.firstName(), profile.lastName()))
                .thenReturn(new Credentials(profile.username(), profile.password()));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer saved = service.create(request);

        assertEquals("Alice.Brown", saved.getUsername());
        assertEquals("password", saved.getPassword());
        assertEquals(yoga, saved.getSpecialization());

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(trainerCaptor.capture());
        assertEquals("Alice", trainerCaptor.getValue().getFirstName());
        assertEquals("Brown", trainerCaptor.getValue().getLastName());
    }

    @Test
    void shouldRejectUnknownTrainingTypeOnCreate() {
        TrainerProfileRequest request = new TrainerProfileRequest("Alice", "Brown", "BOXING");
        when(trainingTypeRepository.findByName("BOXING")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(request));
    }
}
