package com.nikron.weather.controller;

import com.nikron.weather.api.service.WeatherApi;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.ApplicationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(urlPatterns = "/search")
public class SearchController extends BaseController {
    private final WeatherApi api = WeatherApi.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> objectMap = new HashMap<>();
        if (Objects.isNull(req.getParameter("city")) || req.getParameter("city").isBlank()) {
            objectMap.put("locations", new ArrayList<>());
            processTemplate("search", req, resp);
            return;
        }

        try {
            objectMap.put("locations", api.getLocation(req.getParameter("city")));
        } catch (InterruptedException e) {
            throw new ApplicationException("Internal application error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        if (getCookie(req.getCookies(), "weather").isPresent()) {
            objectMap.put("login", ((User) req.getAttribute("user")).getLogin());
            objectMap.put("userId", ((User) req.getAttribute("user")).getId());
        }
        processTemplate("search", objectMap, req, resp);
    }
}
