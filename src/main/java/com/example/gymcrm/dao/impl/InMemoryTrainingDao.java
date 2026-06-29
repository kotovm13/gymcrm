package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.domain.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTrainingDao implements TrainingDao {
    private Map<Long, Training> storage;
    private long sequence;

    @Autowired
    public void setStorage(@Qualifier("trainingStorage") Map<Long, Training> storage) {
        this.storage = storage;
        this.sequence = storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
    }

    @Override
    public Training save(Training training) {
        training.setId(++sequence);
        storage.put(training.getId(), training);

        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }
}
