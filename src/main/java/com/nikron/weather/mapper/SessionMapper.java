package com.nikron.weather.mapper;

import com.nikron.weather.dto.SessionDto;
import com.nikron.weather.entity.Session;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SessionMapper implements Mapper<Session, SessionDto> {

    private final static SessionMapper INSTANCE = new SessionMapper();

    public static SessionMapper getInstance() {
        return INSTANCE;
    }
    @Override
    public SessionDto convertToDto(Session entity) {
        return SessionDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .userLogin(entity.getUser().getLogin())
                .build();
    }

    @Override
    public Session convertToEntity(SessionDto dto) {
        return null;
    }
}
