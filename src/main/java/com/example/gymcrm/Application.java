package com.example.gymcrm;

import com.example.gymcrm.config.AppConfig;
import com.example.gymcrm.facade.GymCrmFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymCrmFacade facade = context.getBean(GymCrmFacade.class);

            System.out.println("Gym CRM started");
            System.out.println("Trainees loaded: " + facade.selectAllTrainees().size());
            System.out.println("Trainers loaded: " + facade.selectAllTrainers().size());
            System.out.println("Trainings loaded: " + facade.selectAllTrainings().size());
        }
    }
}
