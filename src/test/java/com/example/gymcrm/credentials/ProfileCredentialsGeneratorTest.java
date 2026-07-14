package com.example.gymcrm.credentials;

import com.example.gymcrm.repository.TraineeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCredentialsGeneratorTest {
    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Test
    void shouldGenerateUniqueUsernameWithSuffixAndGeneratedPassword() {
        ProfileCredentialsGenerator generator = new ProfileCredentialsGenerator(traineeRepository, passwordGenerator);
        when(traineeRepository.usernameExists("Sam.Green")).thenReturn(true);
        when(traineeRepository.usernameExists("Sam.Green1")).thenReturn(true);
        when(traineeRepository.usernameExists("Sam.Green2")).thenReturn(false);
        when(passwordGenerator.generate()).thenReturn("generated-password");

        Credentials credentials = generator.generate("Sam", "Green");

        assertEquals("Sam.Green2", credentials.username());
        assertEquals("generated-password", credentials.password());
        verify(traineeRepository).usernameExists("Sam.Green");
        verify(traineeRepository).usernameExists("Sam.Green1");
        verify(traineeRepository).usernameExists("Sam.Green2");
    }
}
