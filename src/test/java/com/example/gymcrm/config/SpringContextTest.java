package com.example.gymcrm.config;

import com.example.gymcrm.facade.GymCrmFacade;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SpringContextTest {
    @Test
    void shouldStartContextAndLoadSeedData() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            assertNotNull(facade);
            assertEquals(2, facade.selectAllTrainees().size());
            assertEquals(2, facade.selectAllTrainers().size());
            assertEquals(2, facade.selectAllTrainings().size());
        }
    }
}
