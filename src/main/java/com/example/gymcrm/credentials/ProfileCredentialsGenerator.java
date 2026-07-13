package com.example.gymcrm.credentials;

import com.example.gymcrm.dao.TraineeDao;
import org.springframework.stereotype.Component;

@Component
public class ProfileCredentialsGenerator {
    private final TraineeDao traineeDao;
    private final PasswordGenerator passwordGenerator;

    public ProfileCredentialsGenerator(TraineeDao traineeDao, PasswordGenerator passwordGenerator) {
        this.traineeDao = traineeDao;
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
        return traineeDao.usernameExists(username);
    }
}
