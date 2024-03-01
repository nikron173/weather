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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Instant;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@WebFilter(urlPatterns = "/*")
public class AccessFilter implements Filter {

    private List<String> publicPaths = List.of("/singIn", "/registration");
    private final SessionRepository sessionRepository = SessionRepository.getInstance();
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Cookie[] cookies = req.getCookies();
        Optional<Cookie> cookie = checkCookieWeather(cookies);

        if (cookie.isPresent()) {
            Optional<Session> session = sessionRepository
                    .find(Arrays.stream(cookies)
                            .filter(x -> x.getName().equals("weather"))
                            .findFirst().get().getValue());
            if (session.isPresent()) {
                int check = session.get().getExpiresAt().compareTo(Instant.now());
                if (check == 1) {
                    chain.doFilter(req, resp);
                    return;
                } else {
                    sessionRepository.delete(session.get().getId());
                }
            } else {
                Cookie cookieNew = new Cookie("weather", "");
                cookieNew.setMaxAge(0);
                resp.addCookie(cookieNew);
            }
            resp.sendRedirect(req.getContextPath() + "/signIn");
        } else {
            if (req.getRequestURI().contains("registration")) {
                chain.doFilter(req, resp);
            } else if (req.getRequestURI().contains("signIn")) {
                chain.doFilter(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/signIn");
            }
        }
    }

    private Optional<Cookie> checkCookieWeather (Cookie[] cookies) {
        if (Objects.isNull(cookies)) return Optional.empty();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("weather")) return Optional.of(cookie);
        }
        return Optional.empty();
    }
}
