package com.nikron.weather.service;


import com.nikron.weather.api.dto.ForecastDto;
import com.nikron.weather.api.entity.Forecast;
import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.api.service.WeatherApi;
import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.dto.ShortLocationDto;
import com.nikron.weather.dto.UserDto;
import com.nikron.weather.entity.Location;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.ApplicationException;
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
        throw new NotFoundResourceException("Не найден ресурс с id - " + id,
                HttpServletResponse.SC_NOT_FOUND);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::convertToDto).toList();
    }

    public UserDto create(CreateUserDto dto) {
        System.out.println(dto);
        User user = createUserMapper.convertToEntity(dto);
        System.out.println(user.getPassword());
        if (userRepository.find(user.getLogin()).isEmpty()) {
            return userMapper.convertToDto(userRepository.save(user));
        }
        throw new BadRequestException(String.format("Пользователь с логином %s уже занят", user.getLogin()),
                HttpServletResponse.SC_BAD_REQUEST);
    }

    public boolean verifyUser(UserDto dto) {
        Optional<User> user = userRepository.find(dto.getLogin());
        if (user.isEmpty()) throw new NotFoundResourceException("Пользователь не найден",
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

    public void deleteUserLocation(Long userId, ShortLocationDto dto) {
        Optional<Location> location = locationRepository.find(dto.getName(), dto.getLatitude(), dto.getLongitude());
        if (location.isEmpty()) throw new NotFoundResourceException("Не найдена локация", 400);
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

    public List<ForecastDto> getForecast(Long userId) {
        Optional<User> optionalUser = userRepository.find(userId);
        if (optionalUser.isEmpty()) throw new NotFoundResourceException("Не найден пользователь с id " + userId,
                HttpServletResponse.SC_NOT_FOUND);
        List<Location> locations = userRepository.findUserLocationAll(userId);
        if (locations.isEmpty()) return new ArrayList<>();
        return locations.stream().map(x -> {
            try {
                return api.getForecast(locationMapper.convertToDto(x));
            } catch (IOException | InterruptedException e) {
                System.out.println(e);
                throw new ApplicationException("Internal error api application", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }).toList();
    }
}
