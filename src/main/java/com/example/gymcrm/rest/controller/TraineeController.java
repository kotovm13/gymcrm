package com.example.gymcrm.rest.controller;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.credentials.ProfileRegistration;
import com.example.gymcrm.dto.TraineeProfileRequest;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.rest.auth.AuthCredentials;
import com.example.gymcrm.rest.auth.RequestAuthenticator;
import com.example.gymcrm.rest.dto.CredentialsResponse;
import com.example.gymcrm.rest.dto.TraineeProfileResponse;
import com.example.gymcrm.rest.dto.TraineeTrainersUpdateRequest;
import com.example.gymcrm.rest.dto.TraineeUpdateRequest;
import com.example.gymcrm.rest.dto.TrainerSummaryResponse;
import com.example.gymcrm.rest.mapper.RestResponseMapper;
import com.example.gymcrm.service.TraineeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Trainees")
@Validated
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {
    private final TraineeService traineeService;
    private final RequestAuthenticator requestAuthenticator;
    private final RestResponseMapper mapper;

    public TraineeController(TraineeService traineeService, RequestAuthenticator requestAuthenticator,
                             RestResponseMapper mapper) {
        this.traineeService = traineeService;
        this.requestAuthenticator = requestAuthenticator;
        this.mapper = mapper;
    }

    @ApiOperation("Register trainee")
    @PostMapping
    public ResponseEntity<CredentialsResponse> register(@RequestBody @Valid TraineeProfileRequest request) {
        ProfileRegistration<Trainee> registration = traineeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CredentialsResponse(registration.profile().getUsername(), registration.password()));
    }

    @ApiOperation("Get trainee profile")
    @GetMapping("/{username}")
    public TraineeProfileResponse getProfile(@RequestHeader("Authorization") String authorization,
                                             @PathVariable("username") @NotBlank String username) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        Trainee trainee = traineeService.selectByUsername(username, credentials.password())
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + username));
        return mapper.toTraineeProfile(trainee);
    }

    @ApiOperation("Update trainee profile")
    @PutMapping("/{username}")
    public TraineeProfileResponse updateProfile(@RequestHeader("Authorization") String authorization,
                                                @PathVariable("username") @NotBlank String username,
                                                @RequestBody @Valid TraineeUpdateRequest request) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        requestAuthenticator.requireSameUsername(username, request.username());
        return mapper.toTraineeProfile(traineeService.updateProfile(
                request.username(),
                credentials.password(),
                new TraineeProfileRequest(
                        request.firstName(),
                        request.lastName(),
                        request.dateOfBirth(),
                        request.address()
                ),
                request.active()
        ));
    }

    @ApiOperation("Delete trainee profile")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteProfile(@RequestHeader("Authorization") String authorization,
                                              @PathVariable("username") @NotBlank String username) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        traineeService.deleteByUsername(username, credentials.password());
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get active trainers not assigned to trainee")
    @GetMapping("/{username}/not-assigned-trainers")
    public List<TrainerSummaryResponse> getNotAssignedTrainers(@RequestHeader("Authorization") String authorization,
                                                               @PathVariable("username") @NotBlank String username) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        return traineeService.getNotAssignedTrainers(username, credentials.password()).stream()
                .map(mapper::toTrainerSummary)
                .toList();
    }

    @ApiOperation("Update trainee trainer list")
    @PutMapping("/{username}/trainers")
    public List<TrainerSummaryResponse> updateTrainers(@RequestHeader("Authorization") String authorization,
                                                       @PathVariable("username") @NotBlank String username,
                                                       @RequestBody @Valid TraineeTrainersUpdateRequest request) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        requestAuthenticator.requireSameUsername(username, request.traineeUsername());
        Trainee trainee = traineeService.updateTrainers(
                request.traineeUsername(),
                credentials.password(),
                request.trainers().stream()
                        .map(trainer -> trainer.username())
                        .toList()
        );
        return trainee.getTrainers().stream()
                .map(mapper::toTrainerSummary)
                .toList();
    }

}
