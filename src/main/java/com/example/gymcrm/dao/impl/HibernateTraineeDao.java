package com.example.gymcrm.dao.impl;

import com.example.gymcrm.dao.TraineeDao;
import com.example.gymcrm.domain.Trainee;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HibernateTraineeDao implements TraineeDao {
    private final SessionFactory sessionFactory;

    public HibernateTraineeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Trainee save(Trainee trainee) {
        sessionFactory.getCurrentSession().persist(trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return sessionFactory.getCurrentSession().merge(trainee);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Trainee.class, id));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Trainee t where t.username = :username", Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Override
    public List<Trainee> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Trainee", Trainee.class)
                .getResultList();
    }

    @Override
    public void delete(Trainee trainee) {
        sessionFactory.getCurrentSession().remove(trainee);
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
