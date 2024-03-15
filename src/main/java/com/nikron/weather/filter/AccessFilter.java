package com.nikron.weather.filter;

import com.nikron.weather.entity.Session;
import com.nikron.weather.repository.SessionRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@WebFilter(urlPatterns = "/*")
public class AccessFilter implements Filter {

    private final List<String> publicPaths = List.of("/signIn", "/registration", "/content", "/search");
    private final SessionRepository sessionRepository = SessionRepository.getInstance();
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Cookie[] cookies = req.getCookies();
        Optional<Cookie> cookie = getCookieWeather(cookies);

        if (cookie.isPresent()) {
            Optional<Session> session = sessionRepository
                    .find(cookie.get().getValue());
            if (session.isPresent()) {
                int check = session.get().getExpiresAt().compareTo(Instant.now());
                if (check == 1) {
                    req.setAttribute("user", session.get().getUser());
                    chain.doFilter(req, resp);
                    return;
                } else {
                    sessionRepository.delete(session.get().getId());
                }
            }
            Cookie cookieNew = new Cookie("weather", "");
            cookieNew.setMaxAge(0);
            resp.addCookie(cookieNew);
            req.setAttribute("relogin", "Session has expired. Please re-login");
            req.getRequestDispatcher("/signIn").forward(req, resp);
        } else if (isPublicPath(req.getRequestURI())) {
            chain.doFilter(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/signIn");
        }
    }

    private Optional<Cookie> getCookieWeather (Cookie[] cookies) {
        if (Objects.isNull(cookies)) return Optional.empty();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("weather")) return Optional.of(cookie);
        }
        return Optional.empty();
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::contains);
    }
}
