package com.example.gymcrm.monitoring;

import com.example.gymcrm.exception.AuthenticationException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GymCrmMetricsAspectTest {

    @Test
    void shouldRecordDomainMetrics() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        GymCrmMetricsAspect metrics = new GymCrmMetricsAspect(registry);

        metrics.recordTraineeRegistration();
        metrics.recordTrainerRegistration();
        metrics.recordTrainingCreated();
        metrics.recordAuthenticationFailure(new AuthenticationException("invalid"));

        assertEquals(1, registry.get("gymcrm.profile.registrations")
                .tag("profile.type", "trainee").counter().count());
        assertEquals(1, registry.get("gymcrm.profile.registrations")
                .tag("profile.type", "trainer").counter().count());
        assertEquals(1, registry.get("gymcrm.trainings.added").counter().count());
        assertEquals(1, registry.get("gymcrm.authentication.failures").counter().count());
    }
}
