package com.nikron.weather.service;

import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.repository.LocationRepository;
import com.nikron.weather.util.BuildEntityManagerUtil;
import com.nikron.weather.util.DbMigration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class LocationServiceTest {

    private final LocationRepository locationRepository = LocationRepository.getInstance();
    private final LocationService locationService = LocationService.getInstance();

    @BeforeAll
    public static void init() {
        BuildEntityManagerUtil.initEntityManagerFactory("weather-db-test",
                "jdbc:h2:mem:weatherdb", "h2", "h2");
        DbMigration.dbMigration("org.h2.Driver", "jdbc:h2:mem:weatherdb",
                "h2", "h2");
    }

    @Test
    @DisplayName("saveLocationTest - check save location")
    void saveLocationTest() {
        LocationDto dto = LocationDto.builder()
                .name("Moscow")
                .country("RU")
                .state("Moscow Oblast")
                .latitude(new BigDecimal(53))
                .longitude(new BigDecimal(67))
                .build();
        int beforeCountLocations = locationService.findAll().size();
        locationService.save(dto);
        int afterCountLocations = locationService.findAll().size();
        assertEquals(1, afterCountLocations - beforeCountLocations);
    }

    @Test
    @DisplayName("saveLocationTestThrowDatabase - check method save duplicate location without save in database")
    void saveLocationTestDuplicateLocationNotSave() {
        LocationDto dto = LocationDto.builder()
                .name("Sir")
                .country("RU")
                .state("Oblast Oblast")
                .latitude(new BigDecimal(77))
                .longitude(new BigDecimal(13))
                .build();
        locationService.save(dto);
        int beforeCountLocations = locationService.findAll().size();
        locationService.save(dto);
        int afterCountLocations = locationService.findAll().size();
        assertEquals(0, afterCountLocations - beforeCountLocations);
    }
}