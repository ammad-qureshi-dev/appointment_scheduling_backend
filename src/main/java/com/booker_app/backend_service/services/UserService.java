/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.UUID;

import com.booker_app.backend_service.configs.JwtConfiguration;
import com.booker_app.backend_service.controllers.request.RegistrationRequest;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.repositories.UserRepository;
import com.booker_app.backend_service.utils.HttpUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseType.USER_ALREADY_EXISTS;
import static com.booker_app.backend_service.utils.Constants.Auth.TOKEN;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final JwtConfiguration jwtConfiguration;

	public UserService(UserRepository userRepository, JwtConfiguration jwtConfiguration) {
		this.userRepository = userRepository;
		this.jwtConfiguration = jwtConfiguration;
	}


}
