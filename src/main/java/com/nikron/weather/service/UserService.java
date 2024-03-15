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
import java.util.ArrayList;
import java.util.List;
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
        Optional<User> user = userRepository.find(id);
        if (user.isPresent()) {
            return userMapper.convertToDto(user.get());
        }
        throw new NotFoundResourceException(String.format("User ID \"%d\" not found", id),
                HttpServletResponse.SC_NOT_FOUND);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::convertToDto).toList();
    }

    public UserDto create(CreateUserDto dto) throws BadRequestException {
        User user = createUserMapper.convertToEntity(dto);
        if (userRepository.findByLogin(user.getLogin()).isPresent())
            throw new BadRequestException(String.format("Login \"%s\" is already taken", user.getLogin()),
                    HttpServletResponse.SC_BAD_REQUEST);
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new BadRequestException(String.format("Email \"%s\" is already taken", user.getEmail()),
                    HttpServletResponse.SC_BAD_REQUEST);
        return userMapper.convertToDto(userRepository.save(user));
    }

    public boolean verifyUser(UserDto dto) throws NotFoundResourceException {
        Optional<User> user = userRepository.findByLogin(dto.getLogin());
        if (user.isEmpty()) throw new NotFoundResourceException("Login or password not valid",
                HttpServletResponse.SC_NOT_FOUND);
        dto.setId(user.get().getId());
        return EncodePassword.verifyPassword(dto.getPassword(), user.get().getPassword());
    }

    public UserDto update(Long id, UserDto dto) {
        return null;
    }

    public List<LocationDto> findUserLocationAll(Long userId) {
        return userRepository.findUserLocationAll(userId)
                .stream()
                .map(locationMapper::convertToDto)
                .toList();
    }


    public Location findUserLocation(Long userId, Long locationId) {
        Optional<Location> location = userRepository.findUserLocation(userId, locationId);
        if (location.isEmpty()) throw new NotFoundResourceException(
                String.format("The user with ID \"%d\" you does not have a location with ID \"%d\" or location does not exist",
                        userId, locationId),
                HttpServletResponse.SC_NOT_FOUND);
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
        Optional<Location> location = locationRepository.find(locationId);
        if (location.isEmpty()) throw new NotFoundResourceException(
                String.format("Location with id \"%d\" not found", locationId),
                HttpServletResponse.SC_NOT_FOUND
        );
        userRepository.deleteUserLocation(userId, location.get());
    }

    public void addUserLocation(Long id, LocationDto dto) {
        Location location = locationMapper.convertToEntity(dto);
        Optional<Location> optionalLocation = locationRepository
                .find(location.getName(), location.getLatitude(), location.getLongitude());
        if (optionalLocation.isEmpty()) {
            locationRepository.save(location);
            userRepository.addUserLocation(id, location);
        } else {
            userRepository.addUserLocation(id, optionalLocation.get());
        }
    }

    public List<WeatherDto> getForecast(Long userId) throws NotFoundResourceException {
        Optional<User> optionalUser = userRepository.find(userId);
        if (optionalUser.isEmpty()) throw new NotFoundResourceException(
                String.format("User ID \"%d\" not found", userId),
                HttpServletResponse.SC_NOT_FOUND
        );
        List<Location> locations = userRepository.findUserLocationAll(userId);
        if (locations.isEmpty()) return new ArrayList<>();
        return locations.stream().map(x -> {
            try {
                return api.getWeather(locationMapper.convertToDto(x));
            } catch (IOException | InterruptedException e) {
                throw new WeatherApiException("Network error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }).toList();
    }
}
