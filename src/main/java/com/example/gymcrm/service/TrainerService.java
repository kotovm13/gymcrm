package com.example.gymcrm.service;

import com.example.gymcrm.domain.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer create(Trainer trainer);
    Trainer update(Trainer trainer);
    Optional<Trainer> select(Long id);
    List<Trainer> selectAll();
}
