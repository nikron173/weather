package com.nikron.weather.controller;

import com.nikron.weather.dto.ShortLocationDto;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.ApplicationException;
import com.nikron.weather.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(urlPatterns = "/home")
public class HomeController extends BaseController {

    private final UserService userService = UserService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> objectMap = new HashMap<>();
        User user = (User) req.getAttribute("user");
        objectMap.put("login", user.getLogin());
        objectMap.put("userId", user.getId());
        objectMap.put("forecasts", userService.getForecast(user.getId()));
        processTemplate("home", objectMap, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.isNull(req.getParameter("name")) &&
                Objects.isNull(req.getParameter("latitude")) &&
                Objects.isNull(req.getParameter("longitude"))) throw new ApplicationException("", 500);

        userService.deleteUserLocation(
                ((User) req.getAttribute("user")).getId(), ShortLocationDto.builder()
                                .name(req.getParameter("name"))
                                .latitude(new BigDecimal(req.getParameter("latitude")))
                                .longitude(new BigDecimal(req.getParameter("longitude")))
                                .build()
                );
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}