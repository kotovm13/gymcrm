package com.example.gymcrm.dao;

import com.example.gymcrm.domain.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee save(Trainee trainee);
    Trainee update(Trainee trainee);
    Optional<Trainee> findById(Long id);
    List<Trainee> findAll();
    void deleteById(Long id);
}
