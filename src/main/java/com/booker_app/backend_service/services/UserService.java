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

	public UUID registerUser(RegistrationRequest request, HttpServletResponse response) {
		var userResult = userRepository.getUserByEmail(request.getEmail());
		if (userResult.isPresent()) {
			throw new ServiceResponseException(USER_ALREADY_EXISTS);
		}

		var newUser = User.builder().email(request.getEmail()).password(request.getPassword())
				.dateOfBirth(request.getDateOfBirth()).fullName(request.getFullName())
				.phoneNumber(request.getPhoneNumber()).build();

		userRepository.save(newUser);

		var token = jwtConfiguration.generateToken(newUser);
		HttpUtil.addCookie(response, TOKEN, token);

		return newUser.getId();
	}
}
