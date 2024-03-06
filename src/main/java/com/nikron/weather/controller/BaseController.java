package com.nikron.weather.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BaseController extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(this.getServletContext());
        engine = new TemplateEngine();
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/content/");
        templateResolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        //templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        // Cache is set to true by default. Set to false if you want templates to be automatically updated when modified.
        //templateResolver.setCacheable(true);
        engine.setTemplateResolver(templateResolver);
    }

    protected void processTemplate(String templateName, Map<String, Object> objects, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        IWebExchange webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext context = new WebContext(webExchange);
        for (var object : objects.entrySet()) {
            context.setVariable(object.getKey(), object.getValue());
        }
        engine.process(templateName, context, resp.getWriter());
    }

    protected void processTemplate(String templateName, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        IWebExchange webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext context = new WebContext(webExchange);
        engine.process(templateName, context, resp.getWriter());
    }

    protected Optional<Cookie> getCookie(Cookie[] cookies, String cookieName) {
        if (Objects.isNull(cookies)) return Optional.empty();
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findFirst();
    }
}
