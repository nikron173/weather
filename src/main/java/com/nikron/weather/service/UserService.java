package com.nikron.weather.service;


import com.nikron.weather.api.dto.WeatherDto;
import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.api.exception.WeatherApiException;
import com.nikron.weather.api.service.WeatherApi;
import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.dto.ShortLocationDto;
import com.nikron.weather.dto.UserDto;
import com.nikron.weather.entity.Location;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.BadRequestException;
import com.nikron.weather.exception.NotFoundResourceException;
import com.nikron.weather.mapper.CreateUserMapper;
import com.nikron.weather.mapper.LocationMapper;
import com.nikron.weather.mapper.UserMapper;
import com.nikron.weather.repository.LocationRepository;
import com.nikron.weather.repository.UserRepository;
import com.nikron.weather.util.EncodePassword;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class UserService {

    private final UserRepository userRepository = UserRepository.getInstance();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final CreateUserMapper createUserMapper = CreateUserMapper.getInstance();
    private final LocationRepository locationRepository = LocationRepository.getInstance();

    private final LocationMapper locationMapper = LocationMapper.getInstance();

    private final WeatherApi api = WeatherApi.getInstance();

    private final static UserService INSTANCE = new UserService();

    public static UserService getInstance() {
        return INSTANCE;
    }

    public UserDto find(Long id) {
        log.info("Find user with id - \"{}\"", id);
        Optional<User> user = userRepository.find(id);
        if (user.isPresent()) {
            log.info("User found with id - \"{}\"", id);
            return userMapper.convertToDto(user.get());
        }
        log.error("User ID \"{}\" not found", id);
        throw new NotFoundResourceException(String.format("User ID \"%d\" not found", id),
                HttpServletResponse.SC_NOT_FOUND);
    }

    public List<UserDto> findAll() {
        log.info("Get all users");
        return userRepository.findAll().stream().map(userMapper::convertToDto).toList();
    }

    public UserDto save(CreateUserDto dto) throws BadRequestException {
        log.info("Start process creating user - {}", dto);
        User user = createUserMapper.convertToEntity(dto);
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            log.error("Login \"{}\" is already taken", user.getLogin());
            throw new BadRequestException(String.format("Login \"%s\" is already taken", user.getLogin()),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.error("Email \"{}\" is already taken", user.getEmail());
            throw new BadRequestException(String.format("Email \"%s\" is already taken", user.getEmail()),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        userRepository.save(user);
        log.info("{} - created successfully", user);
        return userMapper.convertToDto(user);
    }

    public boolean verifyUser(UserDto dto) throws NotFoundResourceException {
        Optional<User> user = userRepository.findByLogin(dto.getLogin());
        if (user.isEmpty()) {
            log.error("For user {} not valid login or password", dto);
            throw new NotFoundResourceException("Login or password not valid",
                    HttpServletResponse.SC_NOT_FOUND);
        }
        dto.setId(user.get().getId()); // надо исправить не хорошо так мутировать объекты
        log.info("Verifying that the password is valid for user {}", user);
        return EncodePassword.verifyPassword(dto.getPassword(), user.get().getPassword());
    }

    public List<LocationDto> findUserLocationAll(Long userId) {
        return userRepository.findUserLocationAll(userId)
                .stream()
                .map(locationMapper::convertToDto)
                .toList();
    }


    public Location findUserLocation(Long userId, Long locationId) {
        log.info("Search for a location with an ID \"{}\" for a user with an ID \"{}\"", locationId, userId);
        Optional<Location> location = userRepository.findUserLocation(userId, locationId);
        if (location.isEmpty()) {
            log.error("Location with an ID \"{}\" for a user with an ID \"{}\" not found", locationId, userId);
            throw new NotFoundResourceException(
                    String.format("The user with ID \"%d\" you does not have a location with ID \"%d\" or location does not exist",
                            userId, locationId),
                    HttpServletResponse.SC_NOT_FOUND);
        }
        log.info("Location with an ID \"{}\" for a user with an ID \"{}\" founded", locationId, userId);
        return location.get();
    }

    public void deleteUserLocation(Long userId, ShortLocationDto dto) throws NotFoundResourceException {
        Optional<Location> location = locationRepository.find(dto.getName(), dto.getLatitude(), dto.getLongitude());
        if (location.isEmpty()) throw new NotFoundResourceException(
                String.format("Location \"%s\" not found", dto.getName()),
                HttpServletResponse.SC_NOT_FOUND
        );
        userRepository.deleteUserLocation(userId, location.get());
    }

    public void deleteUserLocation(Long userId, Long locationId) throws NotFoundResourceException {
        log.info("Search for a location with an ID \"{}\" for a user with an ID \"{}\"", locationId, userId);
        Optional<Location> location = locationRepository.find(locationId);
        if (location.isEmpty()) {
            log.error("Location with an ID \"{}\" not found", locationId);
            throw new NotFoundResourceException(
                    String.format("Location with id \"%d\" not found", locationId),
                    HttpServletResponse.SC_NOT_FOUND
            );
        }
        if (!userRepository.deleteUserLocation(userId, location.get())) {
            log.error("Location with an ID \"{}\" for a user with an ID \"{}\" not found", locationId, userId);
            throw new NotFoundResourceException(String.format(
                    "Location with an ID \"%s\" for a user with an ID \"%s\" not found", locationId, userId),
                    HttpServletResponse.SC_NOT_FOUND);
        }
        log.info("Location with ID \"{}\" deleted successfully for a user with an ID \"{}\"", locationId, userId);
    }

    public void addUserLocation(Long id, LocationDto dto) {
        Location location = locationMapper.convertToEntity(dto);
        log.info("Search location \"{}\" in database", location);
        Optional<Location> optionalLocation = locationRepository
                .find(location.getName(), location.getLatitude(), location.getLongitude());
        if (optionalLocation.isEmpty()) {
            throw new NotFoundResourceException(String.format("Location \"%s\" not found", dto.getName()),
                    HttpServletResponse.SC_NOT_FOUND);
        } else {
            userRepository.addUserLocation(id, optionalLocation.get());
            log.info("Location \"{}\" successfully add user with ID \"{}\"", optionalLocation.get(), id);
        }
    }

    public Map<Location, WeatherDto> getForecast(Long userId) throws NotFoundResourceException {
        Optional<User> optionalUser = userRepository.find(userId);
        if (optionalUser.isEmpty()) throw new NotFoundResourceException(
                String.format("User ID \"%d\" not found", userId),
                HttpServletResponse.SC_NOT_FOUND
        );
        List<Location> locations = userRepository.findUserLocationAll(userId);
        Map<Location, WeatherDto> response = new HashMap<>();
        if (locations.isEmpty()) return response;
        for (Location location : locations) {
            try {
                response.put(location, api.getWeather(location));
            } catch (IOException | InterruptedException e) {
                throw new WeatherApiException("Network error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }
}
