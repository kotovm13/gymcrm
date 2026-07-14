package com.example.gymcrm.service;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.TraineeProfileRequest;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee create(@Valid TraineeProfileRequest request);
    boolean authenticate(String username, String password);
    Optional<Trainee> selectByUsername(String username, String password);
    Trainee update(String username, String password, @Valid TraineeProfileRequest request);
    void changePassword(String username, String oldPassword, String newPassword);
    void setActive(String username, String password, boolean active);
    void deleteByUsername(String username, String password);
    List<Training> getTrainings(String username, String password, TraineeTrainingCriteria criteria);
    List<Trainer> getNotAssignedTrainers(String username, String password);
    Trainee updateTrainers(String username, String password, List<String> trainerUsernames);
    void delete(Long id);
    Optional<Trainee> select(Long id);
    List<Trainee> selectAll();
}
