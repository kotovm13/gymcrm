package com.example.gymcrm.rest.mapper;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.domain.User;
import com.example.gymcrm.rest.dto.TraineeProfileResponse;
import com.example.gymcrm.rest.dto.TraineeSummaryResponse;
import com.example.gymcrm.rest.dto.TraineeTrainingResponse;
import com.example.gymcrm.rest.dto.TrainerProfileResponse;
import com.example.gymcrm.rest.dto.TrainerSummaryResponse;
import com.example.gymcrm.rest.dto.TrainerTrainingResponse;
import com.example.gymcrm.rest.dto.TrainingTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RestResponseMapper {
    @Mapping(target = "trainers", expression = "java(toSortedTrainerSummaries(trainee.getTrainers()))")
    TraineeProfileResponse toTraineeProfile(Trainee trainee);

    @Mapping(target = "specialization", source = "specialization.name")
    @Mapping(target = "trainees", expression = "java(toSortedTraineeSummaries(trainer.getTrainees()))")
    TrainerProfileResponse toTrainerProfile(Trainer trainer);

    @Mapping(target = "specialization", source = "specialization.name")
    TrainerSummaryResponse toTrainerSummary(Trainer trainer);

    TraineeSummaryResponse toTraineeSummary(Trainee trainee);

    @Mapping(target = "trainingType", source = "trainingType.name")
    @Mapping(target = "trainerName", expression = "java(fullName(training.getTrainer()))")
    TraineeTrainingResponse toTraineeTraining(Training training);

    @Mapping(target = "trainingType", source = "trainingType.name")
    @Mapping(target = "traineeName", expression = "java(fullName(training.getTrainee()))")
    TrainerTrainingResponse toTrainerTraining(Training training);

    @Mapping(target = "trainingType", source = "name")
    TrainingTypeResponse toTrainingType(TrainingType trainingType);

    List<TrainingTypeResponse> toTrainingTypes(List<TrainingType> trainingTypes);

    default List<TrainerSummaryResponse> toSortedTrainerSummaries(Set<Trainer> trainers) {
        return trainers.stream()
                .map(this::toTrainerSummary)
                .sorted(Comparator.comparing(TrainerSummaryResponse::username))
                .toList();
    }

    default List<TraineeSummaryResponse> toSortedTraineeSummaries(Set<Trainee> trainees) {
        return trainees.stream()
                .map(this::toTraineeSummary)
                .sorted(Comparator.comparing(TraineeSummaryResponse::username))
                .toList();
    }

    default String fullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
