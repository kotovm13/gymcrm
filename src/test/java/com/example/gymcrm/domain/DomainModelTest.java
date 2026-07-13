package com.example.gymcrm.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DomainModelTest {

    @Test
    void shouldCompareTrainingTypesByName() {
        TrainingType yoga = new TrainingType(1L, TrainingType.YOGA);
        TrainingType sameName = new TrainingType(2L, TrainingType.YOGA);
        TrainingType cardio = new TrainingType(3L, TrainingType.CARDIO);

        assertEquals(yoga, sameName);
        assertNotEquals(yoga, cardio);
        assertNotEquals(yoga, "YOGA");
        assertEquals(yoga.hashCode(), sameName.hashCode());
    }

    @Test
    void shouldStoreProfileRelationshipsAndTrainingFields() {
        TrainingType yoga = new TrainingType(1L, TrainingType.YOGA);
        Trainee trainee = new Trainee(1L, "Amy", "Wave", "Amy.Wave", "password", true,
                LocalDate.of(1998, 2, 3), "Address");
        Trainer trainer = new Trainer(2L, "Tom", "Fit", "Tom.Fit", "password", true, yoga);
        Training training = new Training(10L, trainee, trainer, "Yoga Basics", yoga,
                LocalDate.of(2026, 7, 15), 50);

        trainee.setTrainers(new HashSet<>(Set.of(trainer)));
        trainee.setTrainings(new HashSet<>(Set.of(training)));
        trainer.setTrainees(new HashSet<>(Set.of(trainee)));
        trainer.setTrainings(new HashSet<>(Set.of(training)));

        assertEquals(LocalDate.of(1998, 2, 3), trainee.getDateOfBirth());
        assertEquals("Address", trainee.getAddress());
        assertEquals(1, trainee.getTrainers().size());
        assertEquals(1, trainee.getTrainings().size());
        assertEquals(yoga, trainer.getSpecialization());
        assertEquals(1, trainer.getTrainees().size());
        assertEquals(1, trainer.getTrainings().size());
        assertEquals("Yoga Basics", training.getTrainingName());
        assertEquals(50, training.getDurationMinutes());
        assertEquals(training, new Training(10L, trainee, trainer, "Other", yoga, LocalDate.now(), 20));
        assertNotEquals(training, new Training(11L, trainee, trainer, "Other", yoga, LocalDate.now(), 20));
        assertNotEquals(training, "training");
    }

    @Test
    void shouldNotTreatDifferentTransientEntitiesAsEqual() {
        assertNotEquals(new Trainee(), new Trainee());
        assertNotEquals(new Training(), new Training());
    }
}
