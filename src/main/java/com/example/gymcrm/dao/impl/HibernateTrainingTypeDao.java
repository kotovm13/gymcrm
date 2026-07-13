package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HibernateTrainingTypeDao implements TrainingTypeDao {
    private final SessionFactory sessionFactory;

    public HibernateTrainingTypeDao(SessionFactory sessionFactory) {
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
