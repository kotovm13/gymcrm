package com.example.gymcrm.repository;

import com.example.gymcrm.domain.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUsername(String username);
    List<Trainee> findAll();
    void delete(Trainee trainee);
    boolean usernameExists(String username);
}
