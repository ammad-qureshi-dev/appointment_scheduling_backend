/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.filters;

import java.io.IOException;
import java.util.Objects;

import com.booker_app.backend_service.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.booker_app.backend_service.utils.Constants.Auth.AUTH_HEADER;
import static com.booker_app.backend_service.utils.Constants.Auth.BEARER;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader(AUTH_HEADER);
		final String jwt;
		final String username;

		if (Objects.isNull(authHeader) || !authHeader.startsWith(BEARER)) {
			filterChain.doFilter(request, response);
			return;
		}

		jwt = authHeader.substring(BEARER.length());

		username = jwtService.extractUsername(jwt);

		if (!Objects.isNull(username) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (jwtService.isTokenValid(jwt, userDetails)) {
				var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}
