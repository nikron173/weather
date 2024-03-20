package com.nikron.weather.service;

import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.exception.BadRequestException;
import com.nikron.weather.repository.UserRepository;
import com.nikron.weather.util.BuildEntityManagerUtil;
import com.nikron.weather.util.DbMigration;
import com.nikron.weather.util.EnvironmentVariable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserServiceTest {

//    private final UserRepository userRepository = UserRepository.getInstance();
    private final UserService userService = UserService.getInstance();

    @BeforeAll
    public static void init() {
        BuildEntityManagerUtil.initEntityManagerFactory("weather-db-test");
        DbMigration.dbMigration("org.h2.Driver");
    }

    @Test
    @DisplayName("createUserDb - check record in database")
    void createUserDb() {
        int beforeRecords = userService.findAll().size();
        CreateUserDto dto = new CreateUserDto("nikron", "byda", "nikron@kk.com");
        userService.create(dto);
        int afterRecords = userService.findAll().size();
        assertEquals(1, afterRecords-beforeRecords);
    }

    @Test
    @DisplayName("createUserDbThrowNotUniqLogin - check record in database not uniq login")
    void createUserDbThrowNotUniqLogin() {
        CreateUserDto dto1 = new CreateUserDto("login", "byda", "login1@kk.com");
        userService.create(dto1);
        CreateUserDto dto2 = new CreateUserDto("login", "byda", "login2@kk.com");
        assertThrows(BadRequestException.class, () -> userService.create(dto2));
    }

    @Test
    @DisplayName("createUserDbThrowNotUniqEmail - check record in database not uniq email")
    void createUserDbThrowNotUniqEmail() {
        CreateUserDto dto1 = new CreateUserDto("email1", "byda", "email@kk.com");
        userService.create(dto1);
        CreateUserDto dto = new CreateUserDto("email2", "byda", "email@kk.com");
        assertThrows(BadRequestException.class, () -> userService.create(dto));
    }

    @AfterAll
    public static void close() {
        log.info("EntityManagerFactory isOpen - {}", BuildEntityManagerUtil.getEntityManagerFactory().isOpen());
        if (BuildEntityManagerUtil.getEntityManagerFactory().isOpen()) {
            BuildEntityManagerUtil.getEntityManagerFactory().close();
            log.info("EntityManagerFactory close");
        }
    }
}