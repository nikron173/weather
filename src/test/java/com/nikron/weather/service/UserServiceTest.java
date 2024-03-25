package com.nikron.weather.service;

import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.dto.UserDto;
import com.nikron.weather.entity.Location;
import com.nikron.weather.exception.BadRequestException;
import com.nikron.weather.exception.NotFoundResourceException;
import com.nikron.weather.mapper.LocationMapper;
import com.nikron.weather.repository.LocationRepository;
import com.nikron.weather.util.BuildEntityManagerUtil;
import com.nikron.weather.util.DbMigration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Slf4j
class UserServiceTest {
    private final UserService userService = UserService.getInstance();
    private final LocationRepository locationRepository = LocationRepository.getInstance();
    private final LocationMapper locationMapper = LocationMapper.getInstance();

    @BeforeAll
    public static void init() {
        BuildEntityManagerUtil.initEntityManagerFactory("weather-db-test",
                "jdbc:h2:mem:weatherdb", "h2", "h2");
        DbMigration.dbMigration("org.h2.Driver", "jdbc:h2:mem:weatherdb",
                "h2", "h2");
    }

    @Test
    @DisplayName("createUserDb - check record in database")
    void createUserDb() {
        int beforeRecords = userService.findAll().size();
        CreateUserDto dto = new CreateUserDto("nikron", "byda", "nikron@kk.com");
        userService.save(dto);
        int afterRecords = userService.findAll().size();
        assertEquals(1, afterRecords - beforeRecords);
    }

    @Test
    @DisplayName("createUserDbThrowNotUniqLogin - check record in database not uniq login")
    void createUserDbThrowNotUniqLogin() {
        CreateUserDto dto1 = new CreateUserDto("login", "byda", "login1@kk.com");
        userService.save(dto1);
        CreateUserDto dto2 = new CreateUserDto("login", "byda", "login2@kk.com");
        assertThrows(BadRequestException.class, () -> userService.save(dto2));
    }

    @Test
    @DisplayName("createUserDbThrowNotUniqEmail - check record in database not uniq email")
    void createUserDbThrowNotUniqEmail() {
        CreateUserDto dto1 = new CreateUserDto("email1", "byda", "email@kk.com");
        userService.save(dto1);
        CreateUserDto dto = new CreateUserDto("email2", "byda", "email@kk.com");
        assertThrows(BadRequestException.class, () -> userService.save(dto));
    }


    @Test
    @DisplayName("verifyUserTestTrue - check password when true")
    void verifyUserTestTrue() {
        userService.save(new CreateUserDto("kirill", "kkdsad123", "kirill@kk.com"));
        UserDto dto = UserDto.builder().login("kirill").email("kirill@kk.com").password("kkdsad123").build();
        assertTrue(userService.verifyUser(dto));
    }

    @Test
    @DisplayName("verifyUserTestFalse - check password when true")
    void verifyUserTestFalse() {
        userService.save(new CreateUserDto("misha", "kkdsad123", "misha@kk.com"));
        UserDto dto = UserDto.builder().login("misha").email("misha@kk.com").password("kkdsad12").build();
        assertFalse(userService.verifyUser(dto));
    }

    @Test
    @DisplayName("verifyUserAddLocation - check add location for user")
    void verifyUserAddLocation() {
        UserDto dto = userService.save(new CreateUserDto("krug", "krug123", "krug@kk.com"));
        Location location = locationRepository.save(Location.builder()
                .state("Samara Oblast")
                .country("RU")
                .latitude(new BigDecimal("53"))
                .longitude(new BigDecimal("50"))
                .name("Samara")
                .build()
        );
        userService.addUserLocation(dto.getId(), locationMapper.convertToDto(location));
        Location locationDb = userService.findUserLocation(dto.getId(), location.getId());
        assertEquals(location, locationDb);
    }


    @Test
    @DisplayName("verifyUserAddLocationButLocationNotInDb - check add location for user")
    void verifyUserAddLocationButLocationNotInDb() {
        UserDto dto = userService.save(new CreateUserDto("krug1", "krug123", "krug1@kk.com"));
        Location location = Location.builder()
                .state("Kek")
                .country("UI")
                .latitude(new BigDecimal("34"))
                .longitude(new BigDecimal("23"))
                .name("Lokgie")
                .build();

        assertThrows(NotFoundResourceException.class,
                () -> userService.addUserLocation(dto.getId(), locationMapper.convertToDto(location))
        );
    }

    @Test
    @DisplayName("verifyUserAddLocationButLocationNotInDb_ButAddInDB - check add location for user")
    void verifyUserAddLocationButLocationNotInDb_ButAddInDB() {
        UserDto dto = userService.save(new CreateUserDto("krug2", "krug123", "krug2@kk.com"));
        Location location = locationRepository.save(Location.builder()
                .state("Kek")
                .country("UI")
                .latitude(new BigDecimal("35"))
                .longitude(new BigDecimal("25"))
                .name("Lokik")
                .build()
        );

        userService.addUserLocation(dto.getId(), locationMapper.convertToDto(location));
        assertEquals(location, userService.findUserLocation(dto.getId(), location.getId()));
    }

    @Test
    @DisplayName("userDeleteLocationTest - check remove location for user")
    void userDeleteLocationTest() {
        UserDto dto = userService.save(new CreateUserDto("krug3", "krug123", "krug3@kk.com"));
        Location location = locationRepository.save(Location.builder()
                .state("Kek")
                .country("UI")
                .latitude(new BigDecimal("36"))
                .longitude(new BigDecimal("26"))
                .name("Lokik")
                .build()
        );
        userService.addUserLocation(dto.getId(), locationMapper.convertToDto(location));
        int beforeCountLocations = userService.findUserLocationAll(dto.getId()).size();
        userService.deleteUserLocation(dto.getId(), location.getId());
        int afterCountLocations = userService.findUserLocationAll(dto.getId()).size();
        assertEquals(1, beforeCountLocations - afterCountLocations);
    }

    @Test
    @DisplayName("userDeleteLocationButLocationNotInUserTest - check remove location for user")
    void userDeleteLocationButLocationNotInUserTest() {
        UserDto dto = userService.save(new CreateUserDto("krug4", "krug123", "krug4@kk.com"));
        Location location = locationRepository.save(Location.builder()
                .state("Kek")
                .country("UI")
                .latitude(new BigDecimal("36"))
                .longitude(new BigDecimal("26"))
                .name("Lokik")
                .build()
        );
        assertThrows(NotFoundResourceException.class,
                () -> userService.findUserLocation(dto.getId(), 15L)
        );
    }

//    @Test
//    @DisplayName("userDeleteLocationButLocationNotInUserTest - check remove location for user")
//    void userGetForecastLocation() {
//        UserDto dto = userService.save(new CreateUserDto("krug4", "krug123", "krug4@kk.com"));
//        Location location = locationRepository.save(Location.builder()
//                .state("Kek")
//                .country("UI")
//                .latitude(new BigDecimal("36"))
//                .longitude(new BigDecimal("26"))
//                .name("Lokik")
//                .build()
//        );
//        assertThrows(NotFoundResourceException.class,
//                () -> userService.findUserLocation(dto.getId(), 15L)
//        );
//    }

//    @AfterAll
//    public static void close() {
//        log.info("EntityManagerFactory isOpen - {}", BuildEntityManagerUtil.getEntityManagerFactory().isOpen());
//        if (BuildEntityManagerUtil.getEntityManagerFactory().isOpen()) {
//            BuildEntityManagerUtil.getEntityManagerFactory().close();
//            log.info("EntityManagerFactory close");
//        }
//    }
}