package com.example.gymcrm.dao;

import com.example.gymcrm.domain.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer save(Trainer trainer);
    Trainer update(Trainer trainer);
    Optional<Trainer> findById(Long id);
    Optional<Trainer> findByUsername(String username);
    List<Trainer> findAll();
    List<Trainer> findNotAssignedToTrainee(String traineeUsername);
    boolean usernameExists(String username);
}
