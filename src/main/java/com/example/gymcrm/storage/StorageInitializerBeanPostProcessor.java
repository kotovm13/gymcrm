package com.example.gymcrm.storage;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializerBeanPostProcessor implements BeanPostProcessor {

    private final Environment environment;

    public StorageInitializerBeanPostProcessor(Environment environment) {
        this.environment = environment;
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("traineeStorage".equals(beanName)) {
            initializeTrainees(bean);
        } else if ("trainerStorage".equals(beanName)) {
            initializeTrainers(bean);
        } else if ("trainingStorage".equals(beanName)) {
            initializeTrainings(bean);
        }

        return bean;
    }

    @SuppressWarnings("unchecked")
    private void initializeTrainings(Object bean) {
        Map<Long, Training> storage = (Map<Long, Training>) bean;

        readSeedRows().stream()
                .filter(row -> row.startsWith("TRAINING,"))
                .forEach(row -> {
                    var parts = row.split(",", -1);

                    var id = Long.parseLong(parts[1]);

                    var training = new Training(
                            id,
                            Long.valueOf(parts[2]),
                            Long.valueOf(parts[3]),
                            parts[4],
                            TrainingType.valueOf(parts[5]),
                            LocalDate.parse(parts[6]),
                            Integer.parseInt(parts[7])
                    );

                    storage.put(id, training);
                });
    }

    @SuppressWarnings("unchecked")
    private void initializeTrainers(Object bean) {
        Map<Long, Trainer> storage = (Map<Long, Trainer>) bean;

        readSeedRows().stream()
                .filter(row -> row.startsWith("TRAINER,"))
                .forEach(row -> {
                    var parts = row.split(",", -1);

                    var id = Long.parseLong(parts[1]);
                    var trainer = new Trainer(
                            id,
                            parts[2],
                            parts[3],
                            parts[4],
                            parts[5],
                            Boolean.parseBoolean(parts[6]),
                            TrainingType.valueOf(parts[7])
                    );

                    storage.put(id, trainer);
                });
    }

    @SuppressWarnings("unchecked")
    private void initializeTrainees(Object bean) {
        Map<Long, Trainee> storage = (Map<Long, Trainee>) bean;

        readSeedRows().stream()
                .filter(row -> row.startsWith("TRAINEE,"))
                .forEach(row -> {
                    var parts = row.split(",", -1);

                    var id = Long.parseLong(parts[1]);

                    var trainee = new Trainee(
                            id,
                            parts[2],
                            parts[3],
                            parts[4],
                            parts[5],
                            Boolean.parseBoolean(parts[6]),
                            LocalDate.parse(parts[7]),
                            parts[8]
                    );

                    storage.put(id, trainee);
                });
    }

    private List<String> readSeedRows() {
        var location = environment.getRequiredProperty("storage.seed.file");

        if (location.startsWith("classpath:")) {
            var resourceName = location.substring("classpath:".length());

            try (var inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
                if (inputStream == null) {
                    throw new IllegalStateException("Seed file not found: " + resourceName);
                }

                return new String(inputStream.readAllBytes()).lines().toList();
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read seed file: " + resourceName, e);
            }
        }

        try {
            return Files.readAllLines(Path.of(location));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read seed file: " + location, e);
        }
    }
}
