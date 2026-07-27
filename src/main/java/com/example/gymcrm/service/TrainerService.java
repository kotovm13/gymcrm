package com.example.gymcrm.service;

import com.example.gymcrm.credentials.ProfileRegistration;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.TrainerProfileRequest;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    ProfileRegistration<Trainer> create(@Valid TrainerProfileRequest request);
    Trainer updateProfile(String username, String password, @Valid TrainerProfileRequest request, boolean active);
    boolean authenticate(String username, String password);
    Optional<Trainer> selectByUsername(String username, String password);
    Trainer update(String username, String password, @Valid TrainerProfileRequest request);
    void changePassword(String username, String oldPassword, String newPassword);
    void setActive(String username, String password, boolean active);
    List<Training> getTrainings(String username, String password, TrainerTrainingCriteria criteria);
    Optional<Trainer> select(Long id);
    List<Trainer> selectAll();
}
