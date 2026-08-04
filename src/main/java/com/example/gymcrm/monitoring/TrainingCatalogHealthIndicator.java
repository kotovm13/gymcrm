package com.example.gymcrm.monitoring;

import com.example.gymcrm.service.TrainingTypeService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("trainingCatalog")
public class TrainingCatalogHealthIndicator implements HealthIndicator {
    private final TrainingTypeService trainingTypeService;

    public TrainingCatalogHealthIndicator(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @Override
    public Health health() {
        try {
            int trainingTypeCount = trainingTypeService.selectAll().size();
            if (trainingTypeCount == 0) {
                return Health.down().withDetail("reason", "No training types configured").build();
            }
            return Health.up().withDetail("trainingTypes", trainingTypeCount).build();
        } catch (Exception exception) {
            return Health.down(exception).build();
        }
    }
}
