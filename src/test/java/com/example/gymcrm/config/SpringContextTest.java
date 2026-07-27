package com.example.gymcrm.config;

import com.example.gymcrm.facade.GymCrmFacade;
import com.example.gymcrm.rest.aspect.RestControllerLoggingAspect;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpringContextTest {

    @Test
    void shouldLoadApplicationContext() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            assertNotNull(context.getBean(GymCrmFacade.class));
            assertNotNull(context.getBean(TraineeService.class));
            assertNotNull(context.getBean(TrainerService.class));
            assertNotNull(context.getBean(TrainingService.class));
            assertNotNull(context.getBean(UserAccountService.class));
            assertNotNull(context.getBean(RestControllerLoggingAspect.class));
        }
    }
}
