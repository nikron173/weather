package com.nikron.weather.mapper;

import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.entity.User;
import com.nikron.weather.util.EncodePassword;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CreateUserMapper implements Mapper<User, CreateUserDto> {

    private static final CreateUserMapper INSTANCE = new CreateUserMapper();

    public static CreateUserMapper getInstance() {
        return INSTANCE;
    }
    @Override
    public CreateUserDto convertToDto(User entity) {
        return CreateUserDto.builder()
                .login(entity.getLogin())
                .password(entity.getPassword())
                .build();
    }

    @Override
    public User convertToEntity(CreateUserDto dto) {
        return User.builder()
                .login(dto.getLogin())
                .password(EncodePassword.encodePassword(dto.getPassword()))
                .build();
    }
}
