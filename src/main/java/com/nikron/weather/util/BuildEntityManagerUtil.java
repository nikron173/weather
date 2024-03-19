package com.nikron.weather.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class BuildEntityManagerUtil {
    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        log.info("Get entityManagerFactory");
        return emf;
    }

    public static void initEntityManagerFactory() {
        if (Objects.isNull(emf)) {
            try {
                log.info("Create entityManagerFactory");
                Map<String, Object> configOver = new HashMap<>();
                configOver.put("jakarta.persistence.jdbc.url", EnvironmentVariable.URL_DB);
                configOver.put("jakarta.persistence.jdbc.user", EnvironmentVariable.USER_DB);
                configOver.put("jakarta.persistence.jdbc.password", EnvironmentVariable.PASSWORD_DB);
                emf = Persistence
                        .createEntityManagerFactory("weather-db", configOver);
                log.info("Info about entityManagerFactory: isOpen - {}, Properties - {}", emf.isOpen(), emf.getProperties());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
