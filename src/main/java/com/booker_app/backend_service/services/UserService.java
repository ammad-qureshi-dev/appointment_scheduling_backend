/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.UUID;

import com.booker_app.backend_service.controllers.request.UserRegistrationRequest;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseType.USER_ALREADY_EXISTS;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UUID registerUser(UserRegistrationRequest request) {
		var userResult = userRepository.getUserByEmail(request.getEmail());
		if (userResult.isPresent()) {
			throw new ServiceResponseException(USER_ALREADY_EXISTS);
		}

		var newUser = User.builder().email(request.getEmail()).password(request.getPassword())
				.dateOfBirth(request.getDateOfBirth()).fullName(request.getFullName())
				.phoneNumber(request.getPhoneNumber()).build();

		userRepository.save(newUser);

		return newUser.getId();
	}
}
