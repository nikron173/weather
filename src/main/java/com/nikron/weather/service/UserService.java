package com.nikron.weather.service;


import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.dto.UserDto;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.BadRequestException;
import com.nikron.weather.exception.NotFoundResourceException;
import com.nikron.weather.mapper.CreateUserMapper;
import com.nikron.weather.mapper.UserMapper;
import com.nikron.weather.repository.UserRepository;
import com.nikron.weather.util.EncodePassword;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class UserService {

    private final UserRepository userRepository = UserRepository.getInstance();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final CreateUserMapper createUserMapper = CreateUserMapper.getInstance();

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
}
