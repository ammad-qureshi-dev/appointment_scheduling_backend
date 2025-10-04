/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import com.booker_app.backend_service.configs.JwtConfiguration;
import com.booker_app.backend_service.controllers.response.ResponseType;
import com.booker_app.backend_service.controllers.response.dto.UserDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final JwtConfiguration jwtConfiguration;

	public UserService(UserRepository userRepository, JwtConfiguration jwtConfiguration) {
		this.userRepository = userRepository;
		this.jwtConfiguration = jwtConfiguration;
	}

	public UserDTO getUserById(UUID userId) {
		var userOpt = userRepository.getUserById(userId);
		if (userOpt.isEmpty()) {
			throw new ServiceResponseException(ResponseType.USER_NOT_FOUND);
		}

		var user = userOpt.get();
		return UserDTO.builder()
				.email(user.getEmail())
				.fullName(user.getFullName())
				.phoneNumber(user.getPhoneNumber())
				.userId(userId)
				.build();
	}

}
