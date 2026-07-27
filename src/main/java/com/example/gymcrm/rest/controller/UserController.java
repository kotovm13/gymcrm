package com.example.gymcrm.rest.controller;

import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.rest.auth.AuthCredentials;
import com.example.gymcrm.rest.auth.RequestAuthenticator;
import com.example.gymcrm.rest.dto.LoginPasswordChangeRequest;
import com.example.gymcrm.rest.dto.ProfileStatusRequest;
import com.example.gymcrm.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Users")
@Validated
@RestController
public class UserController {
    private final UserAccountService userAccountService;
    private final RequestAuthenticator requestAuthenticator;

    public UserController(UserAccountService userAccountService, RequestAuthenticator requestAuthenticator) {
        this.userAccountService = userAccountService;
        this.requestAuthenticator = requestAuthenticator;
    }

    @ApiOperation("Change trainee or trainer password")
    @PutMapping("/api/users/{username}/password")
    public ResponseEntity<Void> changePassword(@RequestHeader("Authorization") String authorization,
                                               @PathVariable("username") @NotBlank String username,
                                               @RequestBody @Valid LoginPasswordChangeRequest request) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        requestAuthenticator.requireSameUsername(username, request.username());
        if (!credentials.password().equals(request.oldPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }
        userAccountService.changePassword(username, request.oldPassword(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Activate or deactivate user")
    @PatchMapping({
            "/api/users/{username}/status",
            "/api/trainees/{username}/status",
            "/api/trainers/{username}/status"
    })
    public ResponseEntity<Void> updateStatus(@RequestHeader("Authorization") String authorization,
                                             @PathVariable("username") @NotBlank String username,
                                             @RequestBody @Valid ProfileStatusRequest request) {
        AuthCredentials credentials = requestAuthenticator.authenticateSelf(authorization, username);
        requestAuthenticator.requireSameUsername(username, request.username());
        userAccountService.setActive(username, credentials.password(), request.active());
        return ResponseEntity.ok().build();
    }
}
