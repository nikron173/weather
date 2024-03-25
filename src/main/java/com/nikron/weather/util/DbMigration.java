package com.nikron.weather.util;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

@Slf4j
public class DbMigration {
    public static void dbMigration(String DRIVER) {
        log.info("Started database migration");
        Flyway flyway = Flyway.configure()
                .driver(DRIVER)
                .dataSource(
                        EnvironmentVariable.URL_DB,
                        EnvironmentVariable.USER_DB,
                        EnvironmentVariable.PASSWORD_DB
                )
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("End database migration");
    }

    public static void dbMigration(String DRIVER, String URL_DB, String USER_DB, String PASSWORD_DB) {
        log.info("Started database migration");
        Flyway flyway = Flyway.configure()
                .driver(DRIVER)
                .dataSource(
                        URL_DB,
                        USER_DB,
                        PASSWORD_DB
                )
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("End database migration");
    }

    public static void dbMigration(String DRIVER, DataSource dataSource) {
        log.info("Started database migration");
        Flyway flyway = Flyway.configure()
                .driver(DRIVER)
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("End database migration");
    }
}
