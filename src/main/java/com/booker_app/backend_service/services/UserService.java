/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import com.booker_app.backend_service.configs.JwtConfiguration;
import com.booker_app.backend_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final JwtConfiguration jwtConfiguration;

	public UserService(UserRepository userRepository, JwtConfiguration jwtConfiguration) {
		this.userRepository = userRepository;
		this.jwtConfiguration = jwtConfiguration;
	}

}
