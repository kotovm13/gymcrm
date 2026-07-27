package com.example.gymcrm.service.impl;

import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.UserAccountService;
import org.springframework.stereotype.Service;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public UserAccountServiceImpl(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @Override
    public boolean authenticate(String username, String password) {
        return traineeService.authenticate(username, password) || trainerService.authenticate(username, password);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (traineeService.authenticate(username, oldPassword)) {
            traineeService.changePassword(username, oldPassword, newPassword);
            return;
        }
        if (trainerService.authenticate(username, oldPassword)) {
            trainerService.changePassword(username, oldPassword, newPassword);
            return;
        }
        throw new AuthenticationException("Invalid credentials");
    }

    @Override
    public void setActive(String username, String password, boolean active) {
        if (traineeService.authenticate(username, password)) {
            traineeService.setActive(username, password, active);
            return;
        }
        if (trainerService.authenticate(username, password)) {
            trainerService.setActive(username, password, active);
            return;
        }
        throw new AuthenticationException("Invalid credentials");
    }
}
