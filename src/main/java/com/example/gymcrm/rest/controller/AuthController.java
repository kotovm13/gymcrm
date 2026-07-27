package com.example.gymcrm.rest.controller;

import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.rest.auth.AuthCredentials;
import com.example.gymcrm.rest.auth.BasicAuthExtractor;
import com.example.gymcrm.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Authentication")
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAccountService userAccountService;
    private final BasicAuthExtractor authExtractor;

    public AuthController(UserAccountService userAccountService, BasicAuthExtractor authExtractor) {
        this.userAccountService = userAccountService;
        this.authExtractor = authExtractor;
    }

    @ApiOperation("Login as trainee or trainer")
    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestHeader("Authorization") String authorization) {
        AuthCredentials credentials = authExtractor.extract(authorization);
        if (userAccountService.authenticate(credentials.username(), credentials.password())) {
            return ResponseEntity.ok().build();
        }
        throw new AuthenticationException("Invalid credentials");
    }
}
