package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.domain.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTraineeDao implements TraineeDao {
    private Map<Long, Trainee> storage;
    private long sequence;

    @Autowired
    public void setStorage(@Qualifier("traineeStorage") Map<Long, Trainee> storage) {
        this.storage = storage;
        this.sequence = storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
    }

    @Override
    public Trainee save(Trainee trainee) {
        trainee.setId(++sequence);
        storage.put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        storage.put(trainee.getId(), trainee);

        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
