/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.filters;

import java.io.IOException;

import com.booker_app.backend_service.controllers.response.ServiceResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class ServiceResponseFilter extends OncePerRequestFilter {

	private final ObjectFactory<ServiceResponse<?>> serviceResponseFactory;

	public ServiceResponseFilter(ObjectFactory<ServiceResponse<?>> serviceResponseFactory) {
		this.serviceResponseFactory = serviceResponseFactory;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} finally {
			serviceResponseFactory.getObject().getAlerts().clear();
		}
	}
}
