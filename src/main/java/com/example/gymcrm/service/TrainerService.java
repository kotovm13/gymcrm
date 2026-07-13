package com.example.gymcrm.service;

import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.TrainerTrainingCriteria;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer create(Trainer trainer);
    boolean authenticate(String username, String password);
    Optional<Trainer> selectByUsername(String username, String password);
    Trainer update(String username, String password, Trainer trainer);
    void changePassword(String username, String oldPassword, String newPassword);
    void setActive(String username, String password, boolean active);
    List<Training> getTrainings(String username, String password, TrainerTrainingCriteria criteria);

    Trainer update(Trainer trainer);
    Optional<Trainer> select(Long id);
    List<Trainer> selectAll();
}
