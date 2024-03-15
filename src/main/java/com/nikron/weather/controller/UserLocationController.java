package com.nikron.weather.controller;

import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.api.exception.WeatherApiException;
import com.nikron.weather.api.service.WeatherApi;
import com.nikron.weather.entity.Location;
import com.nikron.weather.entity.User;
import com.nikron.weather.exception.ApplicationException;
import com.nikron.weather.service.UserService;
import com.nikron.weather.util.CheckParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/user/location", "/user/location/"})
public class UserLocationController extends BaseController {
    private final UserService userService = UserService.getInstance();
    private final WeatherApi api = WeatherApi.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocationDto dto = LocationDto.builder()
                .name(req.getParameter("name"))
                .country(req.getParameter("country"))
                .state(req.getParameter("state"))
                .longitude(new BigDecimal(req.getParameter("longitude")))
                .latitude(new BigDecimal(req.getParameter("latitude")))
                .build();

        userService.addUserLocation(((User) req.getAttribute("user")).getId(), dto);
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        if (Objects.isNull(req.getParameter("name")) &&
//                Objects.isNull(req.getParameter("latitude")) &&
//                Objects.isNull(req.getParameter("longitude"))) throw new ApplicationException("", 500);

        if (!CheckParameter.checkId(req.getParameter("location_id"))) {
            req.setAttribute("error", "Not valid location id");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            processTemplate("error", req, resp);
            return;
        }


//        userService.deleteUserLocation(
//                ((User) req.getAttribute("user")).getId(), ShortLocationDto.builder()
//                        .name(req.getParameter("name"))
//                        .latitude(new BigDecimal(req.getParameter("latitude")))
//                        .longitude(new BigDecimal(req.getParameter("longitude")))
//                        .build()
//        );

        userService.deleteUserLocation(
                ((User) req.getAttribute("user")).getId(),
                Long.parseLong(req.getParameter("location_id"))
        );
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] str = req.getRequestURI().split("/");
        String strLocationId = str[str.length-1];
        User user = (User) req.getAttribute("user");
        Map<String, Object> objectMap = new HashMap<>();

        if (!CheckParameter.checkId(strLocationId)) {
            objectMap.put("login", user.getLogin());
            objectMap.put("userId", user.getId());
            objectMap.put("error", "Not valid path or location id");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            processTemplate("error", objectMap , req, resp);
            return;
        }
        Long locationId = Long.parseLong(strLocationId);
        try {
            Location location = userService.findUserLocation(user.getId(), locationId);
            objectMap.put("location", location);
            objectMap.put("login", user.getLogin());
            objectMap.put("userId", user.getId());
            objectMap.put("forecasts", api.getForecast(location));
            processTemplate("forecast", objectMap , req, resp);
        } catch (ApplicationException e) {
            objectMap.put("error", e.getError());
            resp.setStatus(e.getCode());
            processTemplate("error", objectMap , req, resp);
        } catch (InterruptedException e) {
            objectMap.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            processTemplate("error", objectMap, req, resp);
        } catch (WeatherApiException e) {
            objectMap.put("error", e.getError());
            resp.setStatus(e.getCode());
            processTemplate("error", objectMap, req, resp);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("DELETE".equals(req.getParameter("_method"))) {
            doDelete(req, resp);
            return;
        }
        super.service(req, resp);
    }
}
