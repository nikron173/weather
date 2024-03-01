package com.nikron.weather.service;

import com.nikron.weather.dto.SessionDto;
import com.nikron.weather.entity.Session;
import com.nikron.weather.exception.NotFoundResourceException;
import com.nikron.weather.mapper.SessionMapper;
import com.nikron.weather.repository.SessionRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SessionService {

    private static final SessionService INSTANCE = new SessionService();
    private final SessionMapper mapper = SessionMapper.getInstance();
    private final SessionRepository sessionRepository = SessionRepository.getInstance();

    public static SessionService getInstance() {
        return INSTANCE;
    }

    public SessionDto find(String id) {
        Optional<Session> session = sessionRepository.find(id);
        if (session.isEmpty()) throw new NotFoundResourceException("Session id " + id + " not found",
                HttpServletResponse.SC_NOT_FOUND);
        return mapper.convertToDto(session.get());
    }
}
