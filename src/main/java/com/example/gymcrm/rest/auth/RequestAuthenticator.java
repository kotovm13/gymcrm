package com.example.gymcrm.rest.auth;

import com.example.gymcrm.exception.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class RequestAuthenticator {
    private final BasicAuthExtractor authExtractor;

    public RequestAuthenticator(BasicAuthExtractor authExtractor) {
        this.authExtractor = authExtractor;
    }

    public AuthCredentials authenticateSelf(String authorization, String username) {
        AuthCredentials credentials = authExtractor.extract(authorization);
        requireSameUsername(username, credentials.username());
        return credentials;
    }

    public void requireSameUsername(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AuthenticationException("Authenticated user does not match requested profile");
        }
    }
}
