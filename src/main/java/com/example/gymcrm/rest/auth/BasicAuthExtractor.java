package com.example.gymcrm.rest.auth;

import com.example.gymcrm.exception.AuthenticationException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthExtractor {
    private static final String BASIC_PREFIX = "Basic ";

    public AuthCredentials extract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_PREFIX)) {
            throw new AuthenticationException("Missing Basic authorization header");
        }

        String encodedCredentials = authorizationHeader.substring(BASIC_PREFIX.length());
        String decodedCredentials;
        try {
            decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new AuthenticationException("Invalid Basic authorization header");
        }
        int separatorIndex = decodedCredentials.indexOf(':');
        if (separatorIndex < 1 || separatorIndex == decodedCredentials.length() - 1) {
            throw new AuthenticationException("Invalid Basic authorization header");
        }

        return new AuthCredentials(
                decodedCredentials.substring(0, separatorIndex),
                decodedCredentials.substring(separatorIndex + 1)
        );
    }
}
