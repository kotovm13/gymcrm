package com.example.gymcrm;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.facade.GymCrmFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            LOGGER.info("Gym CRM started");
            LOGGER.info("Trainees loaded: {}", facade.selectAllTrainees().size());
            LOGGER.info("Trainers loaded: {}", facade.selectAllTrainers().size());
            LOGGER.info("Trainings loaded: {}", facade.selectAllTrainings().size());
        }
    }
}
