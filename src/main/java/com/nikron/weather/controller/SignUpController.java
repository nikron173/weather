package com.nikron.weather.controller;

import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.dto.UserDto;
import com.nikron.weather.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet(urlPatterns = "/registration")
public class SignUpController extends BaseController {

    private final UserService userService = UserService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processTemplate("registration", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDto dto = userService.create(
                CreateUserDto.builder()
                        .login(req.getParameter("login"))
                        .password(req.getParameter("password"))
                        .email(req.getParameter("email"))
                        .build()
        );
        resp.sendRedirect(req.getContextPath() + "/signIn");
    }
}
