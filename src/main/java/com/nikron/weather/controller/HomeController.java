package com.nikron.weather.controller;

import com.nikron.weather.dto.SessionDto;
import com.nikron.weather.entity.Session;
import com.nikron.weather.listener.ThymeleafConfiguration;
import com.nikron.weather.repository.SessionRepository;
import com.nikron.weather.service.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Arrays;

@WebServlet(urlPatterns = "/home")
public class HomeController extends HttpServlet {

    private final SessionService sessionService = SessionService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TemplateEngine engine = (TemplateEngine) getServletContext().getAttribute(ThymeleafConfiguration.THYMELEAF_ENGINE_ATTR);
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext context = new WebContext(webExchange);
        Cookie cookie = Arrays.stream(req.getCookies())
                .filter(x -> x.getName().equals("weather"))
                .findFirst().get();
        System.out.println("Name: " + cookie.getName() + "\nValue: "
                + cookie.getValue());

        SessionDto dto = sessionService.find(cookie.getValue());
        context.setVariable("login", dto.getUserLogin());
        engine.process("main", context, resp.getWriter());
    }
}
