package com.example.gymcrm.config;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class StorageConfig {

    @Bean
    public Map<Long, Trainee> traineeStorage() {
        return new LinkedHashMap<>();
    }

    @Bean
    public Map<Long, Trainer> trainerStorage() {
        return new LinkedHashMap<>();
    }

    @Bean
    public Map<Long, Training> trainingStorage() {
        return new LinkedHashMap<>();
    }
}
