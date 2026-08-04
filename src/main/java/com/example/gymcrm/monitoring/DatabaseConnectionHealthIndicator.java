package com.example.gymcrm.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component("databaseConnection")
public class DatabaseConnectionHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    public DatabaseConnectionHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return Health.up()
                        .withDetail("database", connection.getMetaData().getDatabaseProductName())
                        .build();
            }
            return Health.down().withDetail("reason", "Database connection validation failed").build();
        } catch (Exception exception) {
            return Health.down(exception).build();
        }
    }
}
