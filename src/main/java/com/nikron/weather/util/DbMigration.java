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
                "jdbc:postgresql://localhost:5432/weatherdb",
                "postgres",
                "postgres")
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("End database migration");
    }
}
