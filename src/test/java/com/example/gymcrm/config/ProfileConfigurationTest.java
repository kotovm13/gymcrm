package com.example.gymcrm.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProfileConfigurationTest {

    @Test
    void shouldProvideDistinctDatabaseConfigurationForEveryEnvironment() throws IOException {
        Set<String> urls = new HashSet<>();
        for (String profile : Set.of("local", "dev", "stg", "prod")) {
            Properties properties = load("application-" + profile + ".properties");
            assertNotNull(properties.getProperty("db.driver"));
            assertNotNull(properties.getProperty("db.username"));
            assertNotNull(properties.getProperty("hibernate.hbm2ddl.auto"));
            urls.add(properties.getProperty("db.url"));
        }

        assertEquals(4, urls.size());
        assertNotEquals(load("application-stg.properties").getProperty("db.url"),
                load("application-prod.properties").getProperty("db.url"));
    }

    private Properties load(String resourceName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            assertNotNull(input, resourceName);
            properties.load(input);
        }
        return properties;
    }
}
