package com.nikron.weather.controller;

import com.nikron.weather.api.exception.WeatherApiException;
import com.nikron.weather.api.service.WeatherApi;
import com.nikron.weather.entity.User;
import com.nikron.weather.util.CheckParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/search")
public class SearchController extends BaseController {
    private final WeatherApi api = WeatherApi.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> objectMap = new HashMap<>();
        String city = req.getParameter("city").trim();
        if (!CheckParameter.checkNameCity(city)) {
            objectMap.put("locations", new ArrayList<>());
            req.setAttribute("error", "Not valid city name");
            processTemplate("search", objectMap, req, resp);
            return;
        }

        try {
            objectMap.put("locations", api.getLocation(city));
        } catch (IOException | InterruptedException e) {
            objectMap.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            processTemplate("error", objectMap, req, resp);
            return;
        } catch (WeatherApiException e) {
            objectMap.put("error", e.getError());
            resp.setStatus(e.getCode());
            processTemplate("error", objectMap, req, resp);
            return;
        }
        processTemplate("search", objectMap, req, resp);
    }
}
