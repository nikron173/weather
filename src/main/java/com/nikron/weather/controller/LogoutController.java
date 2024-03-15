package com.nikron.weather.controller;

import com.nikron.weather.service.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = "/logout")
public class LogoutController extends BaseController {

    private final SessionService sessionService = SessionService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<Cookie> cookie = getCookie(req.getCookies());
        if (cookie.isPresent()) {
            sessionService.delete(cookie.get().getValue());
            Cookie cookieNew = new Cookie("weather", "");
            cookieNew.setMaxAge(0);
            resp.addCookie(cookieNew);
        }
        resp.sendRedirect(req.getContextPath() + "/signIn");
    }
}
