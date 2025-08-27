/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.booker_app.backend_service.configs.JwtConfiguration;
import com.booker_app.backend_service.exceptions.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.booker_app.backend_service.utils.Constants.Auth.TOKEN;

@Order(1)
@Component
@WebFilter(urlPatterns = "/api/v1/*")
public class AuthFilter extends OncePerRequestFilter {

	private static final List<String> NON_AUTHORIZED_ENDPOINTS = List.of("/api/v1/auth/login", "/api/v1/auth/register");

	private final JwtConfiguration jwtConfiguration;

	public AuthFilter(JwtConfiguration jwtConfiguration) {
		this.jwtConfiguration = jwtConfiguration;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, AuthenticationException {
		var cookies = request.getCookies();
		var token = getToken(cookies);

		if (jwtConfiguration.isTokenExpired(token)) {
			response.sendError(401, "Token has expired");
			return;
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		var endpoint = request.getRequestURI();
		return NON_AUTHORIZED_ENDPOINTS.stream().anyMatch(e -> e.equalsIgnoreCase(endpoint));
	}

	private static String getToken(Cookie[] cookies) {
		if (Objects.isNull(cookies)) {
			return null;
		}

		return Arrays.stream(cookies).filter(e -> TOKEN.toUpperCase().equals(e.getName())).findFirst().orElseThrow()
				.getValue();
	}
}
