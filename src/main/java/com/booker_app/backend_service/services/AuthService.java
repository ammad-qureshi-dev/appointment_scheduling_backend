/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.LoginRequest;
import com.booker_app.backend_service.controllers.request.RegistrationRequest;
import com.booker_app.backend_service.controllers.response.AuthenticationResponse;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.models.enums.UserRole;
import com.booker_app.backend_service.repositories.BusinessRepository;
import com.booker_app.backend_service.repositories.CustomerRepository;
import com.booker_app.backend_service.repositories.EmployeeRepository;
import com.booker_app.backend_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.booker_app.backend_service.controllers.response.ResponseType.*;
import static com.booker_app.backend_service.models.enums.ContactMethod.EMAIL;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;

@Component
@RequiredArgsConstructor
public class AuthService {

	private final BusinessRepository businessRepository;
	private final EmployeeRepository employeeRepository;
	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final ServiceResponse<?> serviceResponse;
	private final CommunicationService communicationService;

	@Value("${CLIENT_URL}")
	private String CLIENT_URL;

	@Value("${booking-service.secureCookies}")
	private boolean secureCookies;

	public void verifyAccount(UUID userId) {
		var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

		if (user.isVerified()) {
			return;
		}

		user.setVerified(true);
		userRepository.save(user);
	}

	public AuthenticationResponse registerV2(RegistrationRequest request) {
		var findUserResult = userRepository.getUserByPhoneNumberAndEmail(request.getEmail(), request.getEmail());

		if (findUserResult.isPresent()) {
			throw new ServiceResponseException(USER_ALREADY_EXISTS);
		}

		var user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.dateOfBirth(request.getDateOfBirth()).fullName(request.getFullName())
				.phoneNumber(request.getPhoneNumber()).lastSignedInAs(UserRole.CUSTOMER).build();

		userRepository.save(user);

		var token = jwtService.generateToken(user);
		return AuthenticationResponse.builder().token(token).userId(user.getId()).build();
	}

	public AuthenticationResponse loginV2(LoginRequest request) {
		var findUserResult = userRepository.getUserByPhoneNumberAndEmail(request.getPhoneNumber(), request.getEmail());
		User user = null;

		if (findUserResult.isPresent()) {
			user = findUserResult.get();
		} else {
			throw new ServiceResponseException(USER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new ServiceResponseException(INVALID_CREDENTIALS_PROVIDED);
		}

		if (!user.isVerified()) {
			var alerts = serviceResponse.getAlerts();
			alerts.add(generateResponseData(String.valueOf(ACCOUNT_NOT_VERIFIED), ResponseSeverity.WARNING));
		}

		// On first login attempt,
		if (Objects.isNull(user.getLastSignedInAs()) || Objects.isNull(user.getLastUsedContext())) {
			user.setLastSignedInAs(UserRole.CUSTOMER);
			user.setLastUsedContext(user.getId());
			userRepository.save(user);
		}

		var token = jwtService.generateToken(user);

		return AuthenticationResponse.builder().token(token).userId(user.getId()).build();
	}

	public ResponseCookie generateCookie(String token) {
		return ResponseCookie.from("token", token).httpOnly(true).secure(secureCookies).path("/")
				.maxAge(Duration.ofHours(1)).sameSite("Strict").build();
	}
}
