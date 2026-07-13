package com.example.gymcrm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "training_types")
public class TrainingType {
    public static final String STRENGTH = "STRENGTH";
    public static final String CARDIO = "CARDIO";
    public static final String YOGA = "YOGA";
    public static final String FUNCTIONAL = "FUNCTIONAL";

    @Id
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String name;

    public TrainingType() {
    }

    public TrainingType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TrainingType that)) {
            return false;
        }
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
