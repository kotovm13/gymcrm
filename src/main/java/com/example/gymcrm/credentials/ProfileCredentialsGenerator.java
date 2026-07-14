package com.example.gymcrm.credentials;

import com.example.gymcrm.repository.TraineeRepository;
import org.springframework.stereotype.Component;

@Component
public class ProfileCredentialsGenerator {
    private final TraineeRepository traineeRepository;
    private final PasswordGenerator passwordGenerator;

    public ProfileCredentialsGenerator(TraineeRepository traineeRepository, PasswordGenerator passwordGenerator) {
        this.traineeRepository = traineeRepository;
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
        return traineeRepository.usernameExists(username);
    }
}
