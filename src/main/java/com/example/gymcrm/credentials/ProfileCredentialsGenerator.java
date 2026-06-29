package com.example.gymcrm.credentials;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class ProfileCredentialsGenerator {
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Credentials generate(String firstName, String lastName) {
        var baseUsername = firstName + "." + lastName;
        var username = baseUsername;
        int suffix = 1;

        while (usernameExists(username)) {
            username = baseUsername + suffix;
            suffix++;
        }

        var password = passwordGenerator.generate();

        return new Credentials(username, password);
    }

    private boolean usernameExists(String username) {
        return Stream.concat(
                traineeDao.findAll().stream(),
                trainerDao.findAll().stream()
        )
                .map(User::getUsername)
                .filter(Objects::nonNull)
                .anyMatch(username::equals);
    }
}
