package com.example.gymcrm.rest;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.credentials.ProfileRegistration;
import com.example.gymcrm.dto.AddTrainingRequest;
import com.example.gymcrm.dto.TraineeProfileRequest;
import com.example.gymcrm.dto.TraineeTrainingCriteria;
import com.example.gymcrm.dto.TrainerProfileRequest;
import com.example.gymcrm.dto.TrainerTrainingCriteria;
import com.example.gymcrm.exception.AuthenticationException;
import com.example.gymcrm.rest.auth.BasicAuthExtractor;
import com.example.gymcrm.rest.auth.RequestAuthenticator;
import com.example.gymcrm.rest.controller.AuthController;
import com.example.gymcrm.rest.controller.TraineeController;
import com.example.gymcrm.rest.controller.TrainerController;
import com.example.gymcrm.rest.controller.TrainingController;
import com.example.gymcrm.rest.controller.TrainingTypeController;
import com.example.gymcrm.rest.controller.UserController;
import com.example.gymcrm.rest.dto.TrainingTypeResponse;
import com.example.gymcrm.rest.exception.GlobalExceptionHandler;
import com.example.gymcrm.rest.filter.RestLoggingFilter;
import com.example.gymcrm.rest.mapper.RestResponseMapper;
import com.example.gymcrm.service.TraineeService;
import com.example.gymcrm.service.TrainerService;
import com.example.gymcrm.service.TrainingService;
import com.example.gymcrm.service.TrainingTypeService;
import com.example.gymcrm.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestControllerTest {
    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private UserAccountService userAccountService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestResponseMapper mapper = Mappers.getMapper(RestResponseMapper.class);
        BasicAuthExtractor authExtractor = new BasicAuthExtractor();
        RequestAuthenticator requestAuthenticator = new RequestAuthenticator(authExtractor);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new AuthController(userAccountService, authExtractor),
                        new UserController(userAccountService, requestAuthenticator),
                        new TraineeController(traineeService, requestAuthenticator, mapper),
                        new TrainerController(trainerService, requestAuthenticator, mapper),
                        new TrainingController(trainingService, traineeService, trainerService, requestAuthenticator, mapper),
                        new TrainingTypeController(trainingTypeService, userAccountService, authExtractor, mapper)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilters(new RestLoggingFilter())
                .build();
    }

    @Test
    void shouldRegisterTraineeAndTrainer() throws Exception {
        Trainee trainee = trainee("John.Smith", "trainee-pass");
        Trainer trainer = trainer("Sara.Hill", "trainer-pass", TrainingType.YOGA);
        when(traineeService.create(any(TraineeProfileRequest.class)))
                .thenReturn(new ProfileRegistration<>(trainee, "trainee-pass"));
        when(trainerService.create(any(TrainerProfileRequest.class)))
                .thenReturn(new ProfileRegistration<>(trainer, "trainer-pass"));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Smith",
                                  "dateOfBirth": "1995-05-15",
                                  "address": "Main Street"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.password").value("trainee-pass"))
                .andExpect(header().exists("X-Transaction-Id"));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Sara",
                                  "lastName": "Hill",
                                  "specialization": "YOGA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Sara.Hill"))
                .andExpect(jsonPath("$.password").value("trainer-pass"));
    }

    @Test
    void shouldLoginAndChangePassword() throws Exception {
        when(userAccountService.authenticate("John.Smith", "old-pass")).thenReturn(true);

        mockMvc.perform(get("/api/auth/login")
                        .header("Authorization", basic("John.Smith", "old-pass")))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/users/John.Smith/password")
                        .header("Authorization", basic("John.Smith", "old-pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "John.Smith",
                                  "oldPassword": "old-pass",
                                  "newPassword": "new-pass"
                                }
                                """))
                .andExpect(status().isOk());

        verify(userAccountService).changePassword("John.Smith", "old-pass", "new-pass");
    }

    @Test
    void shouldReturnUnauthorizedForInvalidLogin() throws Exception {
        when(userAccountService.authenticate("missing", "bad")).thenReturn(false);

        mockMvc.perform(get("/api/auth/login")
                        .header("Authorization", basic("missing", "bad")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void shouldManageTraineeProfileAndTrainerList() throws Exception {
        Trainee trainee = trainee("John.Smith", "pass");
        Trainer trainer = trainer("Sara.Hill", "trainer-pass", TrainingType.YOGA);
        trainee.setTrainers(Set.of(trainer));
        when(traineeService.selectByUsername("John.Smith", "pass")).thenReturn(Optional.of(trainee));
        when(traineeService.updateProfile(eq("John.Smith"), eq("pass"), any(TraineeProfileRequest.class), eq(true)))
                .thenReturn(trainee);
        when(traineeService.getNotAssignedTrainers("John.Smith", "pass")).thenReturn(List.of(trainer));
        when(traineeService.updateTrainers(eq("John.Smith"), eq("pass"), eq(List.of("Sara.Hill")))).thenReturn(trainee);

        mockMvc.perform(get("/api/trainees/John.Smith")
                        .header("Authorization", basic("John.Smith", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.trainers[0].username").value("Sara.Hill"));

        mockMvc.perform(put("/api/trainees/John.Smith")
                        .header("Authorization", basic("John.Smith", "pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "John.Smith",
                                  "firstName": "John",
                                  "lastName": "Updated",
                                  "dateOfBirth": "1995-05-15",
                                  "address": "New Address",
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"));

        mockMvc.perform(get("/api/trainees/John.Smith/not-assigned-trainers")
                        .header("Authorization", basic("John.Smith", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialization").value(TrainingType.YOGA));

        mockMvc.perform(put("/api/trainees/John.Smith/trainers")
                        .header("Authorization", basic("John.Smith", "pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "John.Smith",
                                  "trainers": [
                                    {"username": "Sara.Hill"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Sara.Hill"));

        mockMvc.perform(patch("/api/users/John.Smith/status")
                        .header("Authorization", basic("John.Smith", "pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "John.Smith",
                                  "active": false
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/trainees/John.Smith")
                        .header("Authorization", basic("John.Smith", "pass"))
                )
                .andExpect(status().isOk());

        verify(userAccountService).setActive("John.Smith", "pass", false);
        verify(traineeService).deleteByUsername("John.Smith", "pass");
    }

    @Test
    void shouldManageTrainerProfileAndStatus() throws Exception {
        Trainer trainer = trainer("Sara.Hill", "pass", TrainingType.YOGA);
        Trainee trainee = trainee("John.Smith", "trainee-pass");
        trainer.setTrainees(Set.of(trainee));
        when(trainerService.selectByUsername("Sara.Hill", "pass")).thenReturn(Optional.of(trainer));
        when(trainerService.updateProfile(eq("Sara.Hill"), eq("pass"), any(TrainerProfileRequest.class), eq(true)))
                .thenReturn(trainer);

        mockMvc.perform(get("/api/trainers/Sara.Hill")
                        .header("Authorization", basic("Sara.Hill", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialization").value(TrainingType.YOGA))
                .andExpect(jsonPath("$.trainees[0].username").value("John.Smith"));

        mockMvc.perform(put("/api/trainers/Sara.Hill")
                        .header("Authorization", basic("Sara.Hill", "pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Sara.Hill",
                                  "firstName": "Sara",
                                  "lastName": "Updated",
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sara.Hill"));

        ArgumentCaptor<TrainerProfileRequest> requestCaptor = ArgumentCaptor.forClass(TrainerProfileRequest.class);
        verify(trainerService).updateProfile(eq("Sara.Hill"), eq("pass"), requestCaptor.capture(), eq(true));
        assertEquals(TrainingType.YOGA, requestCaptor.getValue().specialization());

        mockMvc.perform(patch("/api/trainers/Sara.Hill/status")
                        .header("Authorization", basic("Sara.Hill", "pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Sara.Hill",
                                  "active": false
                                }
                                """))
                .andExpect(status().isOk());

        verify(userAccountService).setActive("Sara.Hill", "pass", false);
    }

    @Test
    void shouldRejectProtectedCallForDifferentAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/trainees/John.Smith")
                        .header("Authorization", basic("Other.User", "pass")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authenticated user does not match requested profile"));
    }

    @Test
    void shouldReturnTrainingListsAndAddTraining() throws Exception {
        Trainee trainee = trainee("John.Smith", "trainee-pass");
        Trainer trainer = trainer("Sara.Hill", "trainer-pass", TrainingType.YOGA);
        Training training = new Training(1L, trainee, trainer, "Morning Yoga", new TrainingType(3L, TrainingType.YOGA),
                LocalDate.of(2026, 7, 27), 60);
        when(traineeService.getTrainings(eq("John.Smith"), eq("trainee-pass"), any(TraineeTrainingCriteria.class)))
                .thenReturn(List.of(training));
        when(trainerService.getTrainings(eq("Sara.Hill"), eq("trainer-pass"), any(TrainerTrainingCriteria.class)))
                .thenReturn(List.of(training));
        when(trainerService.selectByUsername("Sara.Hill", "trainer-pass")).thenReturn(Optional.of(trainer));
        when(trainingService.addTraining(any(AddTrainingRequest.class))).thenReturn(training);

        mockMvc.perform(get("/api/trainees/John.Smith/trainings")
                        .header("Authorization", basic("John.Smith", "trainee-pass"))
                        .param("fromDate", "2026-07-01")
                        .param("toDate", "2026-07-31")
                        .param("trainerName", "Sara")
                        .param("trainingType", TrainingType.YOGA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainerName").value("Sara Hill"));

        mockMvc.perform(get("/api/trainers/Sara.Hill/trainings")
                        .header("Authorization", basic("Sara.Hill", "trainer-pass"))
                        .param("traineeName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].traineeName").value("John Smith"));

        mockMvc.perform(post("/api/trainings")
                        .header("Authorization", basic("Sara.Hill", "trainer-pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "John.Smith",
                                  "trainerUsername": "Sara.Hill",
                                  "trainingName": "Morning Yoga",
                                  "trainingDate": "2026-07-27",
                                  "durationMinutes": 60
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<AddTrainingRequest> requestCaptor = ArgumentCaptor.forClass(AddTrainingRequest.class);
        verify(trainingService).addTraining(requestCaptor.capture());
        assertEquals(TrainingType.YOGA, requestCaptor.getValue().trainingType());
    }

    @Test
    void shouldReturnTrainingTypes() throws Exception {
        when(userAccountService.authenticate("John.Smith", "pass")).thenReturn(true);
        when(trainingTypeService.selectAll()).thenReturn(List.of(
                new TrainingType(1L, TrainingType.STRENGTH),
                new TrainingType(2L, TrainingType.CARDIO)
        ));

        mockMvc.perform(get("/api/training-types")
                        .header("Authorization", basic("John.Smith", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingType").value(TrainingType.STRENGTH));

        TrainingTypeResponse response = new TrainingTypeResponse(3L, TrainingType.YOGA);
        assertEquals(3L, response.id());
        assertEquals(TrainingType.YOGA, response.trainingType());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidBasicHeader() throws Exception {
        mockMvc.perform(get("/api/trainees/John.Smith")
                        .header("Authorization", "Basic invalid"))
                .andExpect(status().isUnauthorized());
    }

    private Trainee trainee(String username, String password) {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setDateOfBirth(LocalDate.of(1995, 5, 15));
        trainee.setAddress("Main Street");
        return trainee;
    }

    private Trainer trainer(String username, String password, String specialization) {
        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setFirstName("Sara");
        trainer.setLastName("Hill");
        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setSpecialization(new TrainingType(3L, specialization));
        return trainer;
    }

    private String basic(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
