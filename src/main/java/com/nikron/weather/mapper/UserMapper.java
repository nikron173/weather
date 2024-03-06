package com.nikron.weather.mapper;

import com.nikron.weather.dto.UserDto;
import com.nikron.weather.entity.User;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UserMapper implements Mapper<User, UserDto> {

    private final static UserMapper INSTANCE = new UserMapper();

    public static UserMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public UserDto convertToDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .email(entity.getEmail())
                .build();
    }

    @Override
    public User convertToEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .login(dto.getLogin())
                .email(dto.getEmail())
                .build();
    }
}
