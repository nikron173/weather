package com.nikron.weather.controller;

import com.nikron.weather.dto.CreateUserDto;
import com.nikron.weather.dto.UserDto;
import com.nikron.weather.exception.ApplicationException;
import com.nikron.weather.service.UserService;
import com.nikron.weather.util.CheckParameter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/registration")
public class SignUpController extends BaseController {

    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processTemplate("registration", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean error = false;
        Map<String, Object> objectMap = new HashMap<>();
        if (!CheckParameter.checkLogin(req.getParameter("login"))) {
            objectMap.put("errorLogin", "Not valid login");
            error = true;
        }
        if (!CheckParameter.checkPassword(req.getParameter("password"))) {
            objectMap.put("errorPasswd",
                    "Not valid password. Length 8 or long and min one upper symbol, min one lower symbol");
            error = true;
        }
        if (!CheckParameter.checkEmail(req.getParameter("email"))) {
            objectMap.put("errorEmail", "Not valid email");
            error = true;
        }

        if (error) {
            processTemplate("registration", objectMap, req, resp);
            return;
        }

        try {
            userService.save(
                    CreateUserDto.builder()
                            .login(req.getParameter("login"))
                            .password(req.getParameter("password"))
                            .email(req.getParameter("email"))
                            .build()
            );
            resp.sendRedirect(req.getContextPath() + "/signIn");
        } catch (ApplicationException e) {
            objectMap.put("error", e.getError());
            processTemplate("registration", objectMap, req, resp);
        }
    }
}
