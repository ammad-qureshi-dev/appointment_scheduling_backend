/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.AuthenticationResponse;
import com.booker_app.backend_service.controllers.response.ResponseType;
import com.booker_app.backend_service.controllers.response.dto.UserDTO;
import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.enums.UserRole;
import com.booker_app.backend_service.repositories.BusinessRepository;
import com.booker_app.backend_service.repositories.CustomerRepository;
import com.booker_app.backend_service.repositories.EmployeeRepository;
import com.booker_app.backend_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseType.*;
import static com.booker_app.backend_service.controllers.response.ResponseType.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BusinessRepository businessRepository;
	private final EmployeeRepository employeeRepository;
	private final CustomerRepository customerRepository;
	private final JwtService jwtService;

	public UserDTO getUserById(UUID userId) {
		var userOpt = userRepository.getUserById(userId);
		if (userOpt.isEmpty()) {
			throw new ServiceResponseException(ResponseType.USER_NOT_FOUND);
		}

		var user = userOpt.get();
		return UserDTO.builder().email(user.getEmail()).fullName(user.getFullName()).phoneNumber(user.getPhoneNumber())
				.userId(userId).lastSignedInAs(user.getLastSignedInAs()).contextId(user.getLastUsedContext())
				.isVerified(user.isVerified()).build();
	}

	public AuthenticationResponse switchUserRole(UUID userId, UUID contextId, UserRole role) {
		var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

		// Verify context exists
		switch (role) {
			// ToDo:
			// case OWNER -> businessRepository.;
			// case CUSTOMER -> customerRepository.findById(contextId)
			// .orElseThrow(() -> new ServiceResponseException(CUSTOMER_NOT_FOUND));
			case EMPLOYEE -> employeeRepository.findById(contextId)
					.orElseThrow(() -> new ServiceResponseException(EMPLOYEE_NOT_FOUND));
		}

		user.setLastSignedInAs(role);
		user.setLastUsedContext(contextId);
		userRepository.save(user);

		var token = jwtService.generateToken(user);
		return AuthenticationResponse.builder().token(token).userId(contextId).build();
	}

	public List<UserProfileDTO> getUserProfiles(UUID userId) {
		var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));
		var profiles = new ArrayList<UserProfileDTO>();
		profiles.add(UserProfileDTO.builder().label(user.getFullName()).contextId(userId)
				.role(UserRole.CUSTOMER.toString()).build());

		profiles.addAll(businessRepository.getBusinessProfiles(userId).orElseGet(ArrayList::new));
		profiles.addAll(employeeRepository.getEmployeeProfiles(userId).orElseGet(ArrayList::new));
		return profiles;
	}

	public UserProfileDTO getCurrentProfile(UUID userId, UUID contextId) {
		// Validate if user exists
		userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

		var userProfiles = getUserProfiles(userId);

		if (Objects.isNull(contextId)) {
			return userProfiles.stream().filter(e -> e.getRole().equals(UserRole.CUSTOMER.toString())).findFirst()
					.orElseThrow(() -> new RuntimeException("No Role Found"));
		}

		var currentProfile = userProfiles.stream().filter(e -> e.getContextId().equals(contextId)).findFirst();
		return currentProfile.orElseThrow(() -> new RuntimeException("Role not found with contextId: " + contextId));

	}

	private boolean profileExists(UserRole role, UUID contextId) {
		if (UserRole.CUSTOMER == role) {
			var value = customerRepository.findById(contextId)
					.orElseThrow(() -> new ServiceResponseException(CUSTOMER_NOT_FOUND));
			return Objects.nonNull(value);
		} else if (UserRole.EMPLOYEE == role) {
			var value = employeeRepository.findById(contextId)
					.orElseThrow(() -> new ServiceResponseException(EMPLOYEE_NOT_FOUND));
			return Objects.nonNull(value);
		} else if (UserRole.OWNER == role) {
			throw new RuntimeException("Not implemented for role = " + role);
		}

		throw new RuntimeException("Role " + role + " does not exist");
	}

}
