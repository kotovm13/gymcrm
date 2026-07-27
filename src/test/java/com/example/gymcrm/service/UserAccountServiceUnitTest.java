package com.example.gymcrm.service;

import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.service.impl.UserAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceUnitTest {
    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    private UserAccountService userAccountService;

    @BeforeEach
    void setUp() {
        userAccountService = new UserAccountServiceImpl(traineeService, trainerService);
    }

    @Test
    void shouldAuthenticateEitherProfileType() {
        when(traineeService.authenticate("trainee", "pass")).thenReturn(true);
        when(traineeService.authenticate("trainer", "pass")).thenReturn(false);
        when(trainerService.authenticate("trainer", "pass")).thenReturn(true);

        assertTrue(userAccountService.authenticate("trainee", "pass"));
        assertTrue(userAccountService.authenticate("trainer", "pass"));
        assertFalse(userAccountService.authenticate("missing", "pass"));
    }

    @Test
    void shouldChangeTrainerPassword() {
        when(traineeService.authenticate("trainer", "old")).thenReturn(false);
        when(trainerService.authenticate("trainer", "old")).thenReturn(true);

        userAccountService.changePassword("trainer", "old", "new");

        verify(trainerService).changePassword("trainer", "old", "new");
    }

    @Test
    void shouldChangeTraineeStatus() {
        when(traineeService.authenticate("trainee", "pass")).thenReturn(true);

        userAccountService.setActive("trainee", "pass", false);

        verify(traineeService).setActive("trainee", "pass", false);
    }

    @Test
    void shouldRejectUnknownUserAccount() {
        assertThrows(AuthenticationException.class,
                () -> userAccountService.changePassword("missing", "old", "new"));
        assertThrows(AuthenticationException.class,
                () -> userAccountService.setActive("missing", "pass", false));
    }
}
