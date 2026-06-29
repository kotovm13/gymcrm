package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TrainerDao;
import com.example.gymcrm.domain.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTrainerDao implements TrainerDao {
    private Map<Long, Trainer> storage;
    private long sequence;

    @Autowired
    public void setStorage(@Qualifier("trainerStorage") Map<Long, Trainer> storage) {
        this.storage = storage;
        this.sequence = storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
    }


    @Override
    public Trainer save(Trainer trainer) {
        trainer.setId(++sequence);
        storage.put(trainer.getId(), trainer);

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        storage.put(trainer.getId(), trainer);

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }
}
