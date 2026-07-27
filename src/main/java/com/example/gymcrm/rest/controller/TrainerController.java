package com.example.gymcrm.rest.controller;

import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.credentials.ProfileRegistration;
import com.example.gymcrm.dto.TrainerProfileRequest;
import com.example.gymcrm.exception.NotFoundException;
import com.example.gymcrm.rest.auth.AuthCredentials;
import com.example.gymcrm.rest.auth.RequestAuthenticator;
import com.example.gymcrm.rest.dto.CredentialsResponse;
import com.example.gymcrm.rest.dto.TrainerProfileResponse;
import com.example.gymcrm.rest.dto.TrainerUpdateRequest;
import com.example.gymcrm.rest.mapper.RestResponseMapper;
import com.example.gymcrm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Trainers")
@Validated
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerService trainerService;
    private final RequestAuthenticator requestAuthenticator;
    private final RestResponseMapper mapper;

    public TrainerController(TrainerService trainerService, RequestAuthenticator requestAuthenticator,
                             RestResponseMapper mapper) {
        this.trainerService = trainerService;
        this.requestAuthenticator = requestAuthenticator;
        this.mapper = mapper;
    }

    @ApiOperation("Register trainer")
    @PostMapping
    public ResponseEntity<CredentialsResponse> register(@RequestBody @Valid TrainerProfileRequest request) {
        ProfileRegistration<Trainer> registration = trainerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CredentialsResponse(registration.profile().getUsername(), registration.password()));
    }

    @ApiOperation("Get trainer profile")
    @GetMapping("/{username}")
    public TrainerProfileResponse getProfile(@RequestHeader("Authorization") String authorization,
                                             @PathVariable("username") @NotBlank String username) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        Trainer trainer = trainerService.selectByUsername(username, credentials.password())
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + username));
        return mapper.toTrainerProfile(trainer);
    }

    @ApiOperation("Update trainer profile")
    @PutMapping("/{username}")
    public TrainerProfileResponse updateProfile(@RequestHeader("Authorization") String authorization,
                                                @PathVariable("username") @NotBlank String username,
                                                @RequestBody @Valid TrainerUpdateRequest request) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        requestAuthenticator.requireSameUsername(username, request.username());
        Trainer existing = trainerService.selectByUsername(request.username(), credentials.password())
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + request.username()));
        return mapper.toTrainerProfile(trainerService.updateProfile(
                request.username(),
                credentials.password(),
                new TrainerProfileRequest(
                        request.firstName(),
                        request.lastName(),
                        existing.getSpecialization().getName()
                ),
                request.active()
        ));
    }

}
