package com.example.gymcrm.monitoring;

import com.example.gymcrm.exception.AuthenticationException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GymCrmMetricsAspect {
    private final Counter traineeRegistrations;
    private final Counter trainerRegistrations;
    private final Counter trainingsCreated;
    private final Counter authenticationFailures;

    @Autowired
    public GymCrmMetricsAspect(ObjectProvider<MeterRegistry> meterRegistryProvider) {
        this(meterRegistryProvider.getIfAvailable(SimpleMeterRegistry::new));
    }

    GymCrmMetricsAspect(MeterRegistry meterRegistry) {
        traineeRegistrations = counter(meterRegistry, "gymcrm.profile.registrations", "trainee");
        trainerRegistrations = counter(meterRegistry, "gymcrm.profile.registrations", "trainer");
        trainingsCreated = Counter.builder("gymcrm.trainings.added")
                .description("Successfully created trainings")
                .register(meterRegistry);
        authenticationFailures = Counter.builder("gymcrm.authentication.failures")
                .description("Rejected authentication attempts")
                .register(meterRegistry);
    }

    @AfterReturning("execution(* com.example.gymcrm.service.impl.TraineeServiceImpl.create(..))")
    public void recordTraineeRegistration() {
        traineeRegistrations.increment();
    }

    @AfterReturning("execution(* com.example.gymcrm.service.impl.TrainerServiceImpl.create(..))")
    public void recordTrainerRegistration() {
        trainerRegistrations.increment();
    }

    @AfterReturning("execution(* com.example.gymcrm.service.impl.TrainingServiceImpl.addTraining(..))")
    public void recordTrainingCreated() {
        trainingsCreated.increment();
    }

    @AfterThrowing(
            pointcut = "execution(* com.example.gymcrm.service.impl..*(..))",
            throwing = "exception"
    )
    public void recordAuthenticationFailure(AuthenticationException exception) {
        authenticationFailures.increment();
    }

    private Counter counter(MeterRegistry registry, String name, String profileType) {
        return Counter.builder(name)
                .description("Successfully registered profiles")
                .tag("profile.type", profileType)
                .register(registry);
    }
}
