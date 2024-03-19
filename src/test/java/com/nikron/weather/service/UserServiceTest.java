package com.nikron.weather.service;

import com.nikron.weather.repository.UserRepository;
import com.nikron.weather.util.BuildEntityManagerUtil;
import com.nikron.weather.util.DbMigration;
import com.nikron.weather.util.EnvironmentVariable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeAll
    public static void init() {

    }

    @Test
    void create() {

    }

    @AfterAll
    public static void close() {

    }
}