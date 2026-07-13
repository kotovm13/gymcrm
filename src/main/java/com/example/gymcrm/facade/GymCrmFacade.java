package com.example.gymcrm.facade;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.dto.TraineeProfileRequest;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.dto.TrainerProfileRequest;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class GymCrmFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymCrmFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Trainee createTrainee(TraineeProfileRequest request) {
        return traineeService.create(toTrainee(request));
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.update(trainee);
    }

    public Trainee updateTrainee(String username, String password, Trainee trainee) {
        return traineeService.update(username, password, trainee);
    }

    public Trainee updateTrainee(String username, String password, TraineeProfileRequest request) {
        return traineeService.update(username, password, toTrainee(request));
    }

    public void deleteTrainee(Long id) {
        traineeService.delete(id);
    }

    public void deleteTrainee(String username, String password) {
        traineeService.deleteByUsername(username, password);
    }

    public Optional<Trainee> selectTrainee(Long id) {
        return traineeService.select(id);
    }

    public Optional<Trainee> selectTrainee(String username, String password) {
        return traineeService.selectByUsername(username, password);
    }

    public List<Trainee> selectAllTrainees() {
        return traineeService.selectAll();
    }

    public boolean authenticateTrainee(String username, String password) {
        return traineeService.authenticate(username, password);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public void setTraineeActive(String username, String password, boolean active) {
        traineeService.setActive(username, password, active);
    }

    public List<Training> getTraineeTrainings(String username, String password, TraineeTrainingCriteria criteria) {
        return traineeService.getTrainings(username, password, criteria);
    }

    public List<Trainer> getNotAssignedTrainers(String username, String password) {
        return traineeService.getNotAssignedTrainers(username, password);
    }

    public Trainee updateTraineeTrainers(String username, String password, List<String> trainerUsernames) {
        return traineeService.updateTrainers(username, password, trainerUsernames);
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer createTrainer(TrainerProfileRequest request) {
        return trainerService.create(toTrainer(request));
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Trainer updateTrainer(String username, String password, Trainer trainer) {
        return trainerService.update(username, password, trainer);
    }

    public Trainer updateTrainer(String username, String password, TrainerProfileRequest request) {
        return trainerService.update(username, password, toTrainer(request));
    }

    public Optional<Trainer> selectTrainer(Long id) {
        return trainerService.select(id);
    }

    public Optional<Trainer> selectTrainer(String username, String password) {
        return trainerService.selectByUsername(username, password);
    }

    public List<Trainer> selectAllTrainers() {
        return trainerService.selectAll();
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerService.authenticate(username, password);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public void setTrainerActive(String username, String password, boolean active) {
        trainerService.setActive(username, password, active);
    }

    public List<Training> getTrainerTrainings(String username, String password, TrainerTrainingCriteria criteria) {
        return trainerService.getTrainings(username, password, criteria);
    }

    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Training addTraining(String trainerUsername, String trainerPassword, String traineeUsername,
                                String trainingName, String trainingType, LocalDate trainingDate, int durationMinutes) {
        return trainingService.addTraining(
                trainerUsername,
                trainerPassword,
                traineeUsername,
                trainingName,
                trainingType,
                trainingDate,
                durationMinutes
        );
    }

    public Training addTraining(AddTrainingRequest request) {
        return trainingService.addTraining(
                request.trainerUsername(),
                request.trainerPassword(),
                request.traineeUsername(),
                request.trainingName(),
                request.trainingType(),
                request.trainingDate(),
                request.durationMinutes()
        );
    }

    public Optional<Training> selectTraining(Long id) {
        return trainingService.select(id);
    }

    public List<Training> selectAllTrainings() {
        return trainingService.selectAll();
    }

    private Trainee toTrainee(TraineeProfileRequest request) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(request.firstName());
        trainee.setLastName(request.lastName());
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());
        return trainee;
    }

    private Trainer toTrainer(TrainerProfileRequest request) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        trainer.setSpecialization(new TrainingType(null, request.specialization()));
        return trainer;
    }
}
