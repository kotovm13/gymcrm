package com.example.gymcrm.repository;

import com.example.gymcrm.domain.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    Optional<TrainingType> findByName(String name);
    List<TrainingType> findAll();
}
