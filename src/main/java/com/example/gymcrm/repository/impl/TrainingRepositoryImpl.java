package com.example.gymcrm.repository.impl;

import com.example.gymcrm.repository.TrainingRepository;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private final SessionFactory sessionFactory;

    public TrainingRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Training save(Training training) {
        sessionFactory.getCurrentSession().persist(training);
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Training.class, id));
    }

    @Override
    public List<Training> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Training", Training.class)
                .getResultList();
    }

    @Override
    public List<Training> findByTraineeCriteria(String traineeUsername, TraineeTrainingCriteria criteria) {
        String query = """
                select tr from Training tr
                join fetch tr.trainer trainer
                join fetch tr.trainee trainee
                join fetch tr.trainingType type
                where trainee.username = :username
                and (:fromDate is null or tr.trainingDate >= :fromDate)
                and (:toDate is null or tr.trainingDate <= :toDate)
                and (:trainerName is null or lower(concat(trainer.firstName, ' ', trainer.lastName)) like lower(concat('%', :trainerName, '%')))
                and (:trainingType is null or type.name = :trainingType)
                """;
        return sessionFactory.getCurrentSession()
                .createQuery(query, Training.class)
                .setParameter("username", traineeUsername)
                .setParameter("fromDate", criteria.fromDate())
                .setParameter("toDate", criteria.toDate())
                .setParameter("trainerName", criteria.trainerName())
                .setParameter("trainingType", criteria.trainingType())
                .getResultList();
    }

    @Override
    public List<Training> findByTrainerCriteria(String trainerUsername, TrainerTrainingCriteria criteria) {
        String query = """
                select tr from Training tr
                join fetch tr.trainer trainer
                join fetch tr.trainee trainee
                join fetch tr.trainingType type
                where trainer.username = :username
                and (:fromDate is null or tr.trainingDate >= :fromDate)
                and (:toDate is null or tr.trainingDate <= :toDate)
                and (:traineeName is null or lower(concat(trainee.firstName, ' ', trainee.lastName)) like lower(concat('%', :traineeName, '%')))
                """;
        return sessionFactory.getCurrentSession()
                .createQuery(query, Training.class)
                .setParameter("username", trainerUsername)
                .setParameter("fromDate", criteria.fromDate())
                .setParameter("toDate", criteria.toDate())
                .setParameter("traineeName", criteria.traineeName())
                .getResultList();
    }
}
