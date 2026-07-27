package com.example.gymcrm.rest.controller;

import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.rest.auth.AuthCredentials;
import com.example.gymcrm.rest.auth.BasicAuthExtractor;
import com.example.gymcrm.rest.dto.TrainingTypeResponse;
import com.example.gymcrm.rest.mapper.RestResponseMapper;
import com.example.gymcrm.service.TrainingTypeService;
import com.example.gymcrm.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Training types")
@RestController
@RequestMapping("/api/training-types")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;
    private final UserAccountService userAccountService;
    private final BasicAuthExtractor authExtractor;
    private final RestResponseMapper mapper;

    public TrainingTypeController(TrainingTypeService trainingTypeService, UserAccountService userAccountService,
                                  BasicAuthExtractor authExtractor, RestResponseMapper mapper) {
        this.trainingTypeService = trainingTypeService;
        this.userAccountService = userAccountService;
        this.authExtractor = authExtractor;
        this.mapper = mapper;
    }

    @ApiOperation("Get training types")
    @GetMapping
    public List<TrainingTypeResponse> getTrainingTypes(@RequestHeader("Authorization") String authorization) {
        AuthCredentials credentials = authExtractor.extract(authorization);
        if (!userAccountService.authenticate(credentials.username(), credentials.password())) {
            throw new AuthenticationException("Invalid credentials");
        }
        return mapper.toTrainingTypes(trainingTypeService.selectAll());
    }
}
