package com.example.gymcrm.service;

import com.example.gymcrm.domain.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee create(Trainee trainee);
    Trainee update(Trainee trainee);
    void delete(Long id);
    Optional<Trainee> select(Long id);
    List<Trainee> selectAll();
}
