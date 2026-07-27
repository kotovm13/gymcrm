package com.example.gymcrm.rest.controller;

import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.rest.auth.AuthCredentials;
import com.example.gymcrm.rest.auth.RequestAuthenticator;
import com.example.gymcrm.rest.dto.RestAddTrainingRequest;
import com.example.gymcrm.rest.dto.TraineeTrainingResponse;
import com.example.gymcrm.rest.dto.TrainerTrainingResponse;
import com.example.gymcrm.rest.mapper.RestResponseMapper;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "Trainings")
@Validated
@RestController
@RequestMapping("/api")
public class TrainingController {
    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final RequestAuthenticator requestAuthenticator;
    private final RestResponseMapper mapper;

    public TrainingController(TrainingService trainingService, TraineeService traineeService, TrainerService trainerService,
                              RequestAuthenticator requestAuthenticator, RestResponseMapper mapper) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.requestAuthenticator = requestAuthenticator;
        this.mapper = mapper;
    }

    @ApiOperation("Get trainee trainings list")
    @GetMapping("/trainees/{username}/trainings")
    public List<TraineeTrainingResponse> getTraineeTrainings(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("username") @NotBlank String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "trainerName", required = false) String trainerName,
            @RequestParam(name = "trainingType", required = false) String trainingType) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        return traineeService.getTrainings(
                        username,
                        credentials.password(),
                        new TraineeTrainingCriteria(fromDate, toDate, trainerName, trainingType)
                ).stream()
                .map(mapper::toTraineeTraining)
                .toList();
    }

    @ApiOperation("Get trainer trainings list")
    @GetMapping("/trainers/{username}/trainings")
    public List<TrainerTrainingResponse> getTrainerTrainings(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("username") @NotBlank String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "traineeName", required = false) String traineeName) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        return trainerService.getTrainings(
                        username,
                        credentials.password(),
                        new TrainerTrainingCriteria(fromDate, toDate, traineeName)
                ).stream()
                .map(mapper::toTrainerTraining)
                .toList();
    }

    @ApiOperation("Add training")
    @PostMapping("/trainings")
    public ResponseEntity<Void> addTraining(@RequestHeader("Authorization") String authorization,
                                            @RequestBody @Valid RestAddTrainingRequest request) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, request.trainerUsername());
        Trainer trainer = trainerService.selectByUsername(request.trainerUsername(), credentials.password())
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + request.trainerUsername()));
        trainingService.addTraining(new AddTrainingRequest(
                request.trainerUsername(),
                credentials.password(),
                request.traineeUsername(),
                request.trainingName(),
                trainer.getSpecialization().getName(),
                request.trainingDate(),
                request.durationMinutes()
        ));
        return ResponseEntity.ok().build();
    }
}
