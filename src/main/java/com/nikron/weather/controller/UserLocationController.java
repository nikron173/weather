package com.nikron.weather.controller;

import com.nikron.weather.api.dto.LocationDto;
import com.nikron.weather.entity.User;
import com.nikron.weather.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = "/user/location")
public class UserLocationController extends BaseController{
    private final UserService userService = UserService.getInstance();
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
        super.doDelete(req, resp);
    }

}
