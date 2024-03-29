package com.nikron.weather.controller;

import com.nikron.weather.dto.UserDto;
import com.nikron.weather.entity.Session;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.ApplicationException;
import com.nikron.weather.exception.NotFoundResourceException;
import com.nikron.weather.mapper.Mapper;
import com.nikron.weather.mapper.UserMapper;
import com.nikron.weather.repository.SessionRepository;
import com.nikron.weather.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@WebServlet(urlPatterns = "/signIn")
public class SignInController extends BaseController {

    private final UserService userService = UserService.getInstance();
    private final SessionRepository sessionRepository = SessionRepository.getInstance();

    private final Mapper<User, UserDto> mapper = UserMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processTemplate("login", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> objectMap = new HashMap<>();
        if ((Objects.isNull(req.getParameter("login")) || req.getParameter("login").isBlank()) ||
                (Objects.isNull(req.getParameter("password")) || req.getParameter("password").isBlank())) {
            objectMap.put("error", "Login or password cannot be blank or contain spaces");
            processTemplate("login", objectMap, req, resp);
            return;
        }
        UserDto dto = UserDto.builder()
                .login(req.getParameter("login"))
                .password(req.getParameter("password"))
                .build();

        try {
            if (userService.verifyUser(dto)) {
                Session session = new Session(UUID.randomUUID().toString(),
                        mapper.convertToEntity(dto),
                        Instant.now().plusSeconds(1800)
                );
                sessionRepository.save(session);
                Cookie cookie = new Cookie("weather", session.getId());
                cookie.setMaxAge(3600);
                resp.addCookie(cookie);
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            objectMap.put("error", "Login or password not valid");
            processTemplate("login", objectMap, req, resp);
        } catch (NotFoundResourceException e) {
            objectMap.put("error", e.getError());
            resp.setStatus(e.getCode());
            processTemplate("login", objectMap, req, resp);
        } catch (ApplicationException e) {
            req.setAttribute("error", e.getError());
            processTemplate("error", req, resp);
        }
    }
}
