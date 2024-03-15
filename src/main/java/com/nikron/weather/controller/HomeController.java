package com.nikron.weather.controller;

import com.nikron.weather.api.exception.WeatherApiException;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.ApplicationException;
import com.nikron.weather.exception.NotFoundResourceException;
import com.nikron.weather.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/home")
public class HomeController extends BaseController {

    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> objectMap = new HashMap<>();
        User user = (User) req.getAttribute("user");
        objectMap.put("login", user.getLogin());
        objectMap.put("userId", user.getId());
        try {
            objectMap.put("forecasts", userService.getForecast(user.getId()));
        } catch (WeatherApiException e) {
            objectMap.put("error", e.getError());
            resp.setStatus(e.getCode());
            processTemplate("error", req, resp);
            return;
        }
        processTemplate("home", objectMap, req, resp);
    }
}