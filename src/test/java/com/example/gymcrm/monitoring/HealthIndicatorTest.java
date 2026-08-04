package com.example.gymcrm.monitoring;

import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.service.TrainingTypeService;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthIndicatorTest {

    @Test
    void shouldReportDatabaseUpForValidConnection() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.getDatabaseProductName()).thenReturn("H2");

        var health = new DatabaseConnectionHealthIndicator(dataSource).health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("H2", health.getDetails().get("database"));
    }

    @Test
    void shouldReportDatabaseDownWhenConnectionFails() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new IllegalStateException("offline"));

        var health = new DatabaseConnectionHealthIndicator(dataSource).health();

        assertEquals("DOWN", health.getStatus().getCode());
    }

    @Test
    void shouldReportTrainingCatalogStatus() {
        TrainingTypeService service = mock(TrainingTypeService.class);
        when(service.selectAll()).thenReturn(List.of(new TrainingType(1L, "Fitness")));
        var indicator = new TrainingCatalogHealthIndicator(service);

        assertEquals("UP", indicator.health().getStatus().getCode());

        when(service.selectAll()).thenReturn(List.of());
        assertEquals("DOWN", indicator.health().getStatus().getCode());
    }
}
