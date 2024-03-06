package com.nikron.weather.util;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

@Slf4j
public class DbMigration {
    public static void dbMigration() {

        log.info("Started database migration");
        Flyway flyway = Flyway.configure()
                .driver("org.postgresql.Driver")
                .dataSource(
                EnvironmentVariable.URL_DB,
                EnvironmentVariable.USER_DB,
                EnvironmentVariable.PASSWORD_DB)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("End database migration");
    }
}
