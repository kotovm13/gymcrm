package com.example.gymcrm.repository.impl;

import com.example.gymcrm.repository.TrainerRepository;
import com.example.gymcrm.domain.Trainer;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private final SessionFactory sessionFactory;

    public TrainerRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Trainer save(Trainer trainer) {
        sessionFactory.getCurrentSession().persist(trainer);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        return sessionFactory.getCurrentSession().merge(trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Trainer.class, id));
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Trainer t where t.username = :username", Trainer.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Override
    public List<Trainer> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Trainer", Trainer.class)
                .getResultList();
    }

    @Override
    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
        return sessionFactory.getCurrentSession()
                .createQuery("""
                        select tr from Trainer tr
                        where tr.id not in (
                            select assigned.id from Trainee te
                            join te.trainers assigned
                            where te.username = :username
                        )
                        """, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    @Override
    public boolean usernameExists(String username) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("select count(u.id) from User u where u.username = :username", Long.class)
                .setParameter("username", username)
                .uniqueResult();
        return count != null && count > 0;
    }
}
