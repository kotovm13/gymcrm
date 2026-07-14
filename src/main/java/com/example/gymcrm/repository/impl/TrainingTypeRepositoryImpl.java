package com.example.gymcrm.repository.impl;

import com.example.gymcrm.repository.TrainingTypeRepository;
import com.example.gymcrm.domain.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    private final SessionFactory sessionFactory;

    public TrainingTypeRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        return sessionFactory.getCurrentSession()
                .createQuery("from TrainingType t where t.name = :name", TrainingType.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }
}
