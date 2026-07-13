package com.example.gymcrm;

import com.example.gymcrm.domain.Trainee;
import com.example.gymcrm.domain.Trainer;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.dto.TraineeProfileRequest;
import com.example.gymcrm.dto.TrainerProfileRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CsvTestData {
    private static final Map<String, ProfileRow> PROFILES = load("test-data/profiles.csv", ProfileRow::from);
    private static final Map<String, TrainingRow> TRAININGS = load("test-data/trainings.csv", TrainingRow::from);

    private CsvTestData() {
    }

    public static TraineeProfileRequest traineeRequest(String key) {
        ProfileRow row = profile(key);
        return new TraineeProfileRequest(row.firstName(), row.lastName(), row.dateOfBirth(), row.address());
    }

    public static TrainerProfileRequest trainerRequest(String key) {
        ProfileRow row = profile(key);
        return new TrainerProfileRequest(row.firstName(), row.lastName(), row.trainingType());
    }

    public static Trainee trainee(String key) {
        ProfileRow row = profile(key);
        Trainee trainee = new Trainee();
        trainee.setFirstName(row.firstName());
        trainee.setLastName(row.lastName());
        trainee.setDateOfBirth(row.dateOfBirth());
        trainee.setAddress(row.address());
        trainee.setUsername(row.username());
        trainee.setPassword(row.password());
        return trainee;
    }

    public static Trainer trainer(String key) {
        ProfileRow row = profile(key);
        Trainer trainer = new Trainer();
        trainer.setFirstName(row.firstName());
        trainer.setLastName(row.lastName());
        trainer.setSpecialization(new TrainingType(null, row.trainingType()));
        trainer.setUsername(row.username());
        trainer.setPassword(row.password());
        return trainer;
    }

    public static ProfileRow profile(String key) {
        return required(PROFILES, key, "profile");
    }

    public static TrainingRow training(String key) {
        return required(TRAININGS, key, "training");
    }

    private static <T extends KeyedRow> Map<String, T> load(String resourcePath, Function<String[], T> mapper) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                requiredResource(resourcePath),
                StandardCharsets.UTF_8
        ))) {
            return reader.lines()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(line -> line.split(",", -1))
                    .map(mapper)
                    .collect(Collectors.toMap(KeyedRow::key, Function.identity()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read test data from " + resourcePath, e);
        }
    }

    private static java.io.InputStream requiredResource(String resourcePath) {
        java.io.InputStream inputStream = CsvTestData.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalStateException("Missing test resource: " + resourcePath);
        }
        return inputStream;
    }

    private static <T> T required(Map<String, T> rows, String key, String type) {
        T row = rows.get(key);
        if (row == null) {
            throw new IllegalArgumentException("Unknown " + type + " test data key: " + key);
        }
        return row;
    }

    private static LocalDate date(String value) {
        return value.isBlank() ? null : LocalDate.parse(value);
    }

    private static int integer(String value) {
        return Integer.parseInt(value);
    }

    private interface KeyedRow {
        String key();
    }

    public record ProfileRow(
            String key,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String address,
            String trainingType,
            String username,
            String password
    ) implements KeyedRow {
        private static ProfileRow from(String[] columns) {
            requireColumnCount(columns, 8);
            return new ProfileRow(
                    columns[0],
                    columns[1],
                    columns[2],
                    date(columns[3]),
                    columns[4],
                    columns[5],
                    columns[6],
                    columns[7]
            );
        }
    }

    public record TrainingRow(
            String key,
            String trainingName,
            String trainingType,
            LocalDate trainingDate,
            int durationMinutes,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerNameFilter,
            String traineeNameFilter
    ) implements KeyedRow {
        private static TrainingRow from(String[] columns) {
            requireColumnCount(columns, 9);
            return new TrainingRow(
                    columns[0],
                    columns[1],
                    columns[2],
                    date(columns[3]),
                    integer(columns[4]),
                    date(columns[5]),
                    date(columns[6]),
                    columns[7],
                    columns[8]
            );
        }
    }

    private static void requireColumnCount(String[] columns, int expected) {
        if (columns.length != expected) {
            throw new IllegalArgumentException("Expected " + expected + " CSV columns but got "
                    + columns.length + ": " + Arrays.toString(columns));
        }
    }
}
