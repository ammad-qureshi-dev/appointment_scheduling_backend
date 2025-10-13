/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.CommsRequest;
import com.booker_app.backend_service.controllers.request.LoginRequest;
import com.booker_app.backend_service.controllers.request.RegistrationRequest;
import com.booker_app.backend_service.controllers.response.AuthenticationResponse;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.models.enums.ContactMethod;
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
import static com.booker_app.backend_service.models.enums.ContactMethod.PHONE;
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

	public boolean verifyAccount(UUID userId, ContactMethod method) {
		var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

		if (user.isVerified()) {
			return true;
		}

		if (method.equals(EMAIL)) {
			user.setVerified(true);
			userRepository.save(user);
			return true;
		} else if (PHONE.equals(method)) {
			// ToDo: implement account verification via OTP
			throw new ServiceResponseException(NOT_IMPLEMENTED_YET);
		}

		return false;
	}

	public AuthenticationResponse registerV2(RegistrationRequest request) {
		var findUserResult = userRepository.getUserByPhoneNumberAndEmail(request.getEmail(), request.getEmail());

		if (findUserResult.isPresent()) {
			throw new ServiceResponseException(USER_ALREADY_EXISTS);
		}

		var user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.dateOfBirth(request.getDateOfBirth()).fullName(request.getFullName())
				.phoneNumber(request.getPhoneNumber()).build();

		userRepository.save(user);

		var token = jwtService.generateToken(user);

		if (!Objects.isNull(user.getEmail())) {

			var verificationLink = CLIENT_URL + "/auth/verify-account/" + user.getId();

			var commsRequest = CommsRequest.builder()
					.commsType(EMAIL)
					.recipient(user.getEmail())
					.subject("Verify Email")
					.messageContent("Verify your account here: " + verificationLink)
					.build();
			communicationService.sendCommunication(EMAIL, commsRequest);
		}

		return AuthenticationResponse.builder().token(token).userId(user.getId()).build();
	}

	public AuthenticationResponse loginV2(LoginRequest request) {

		String username;

		if (request.getLoginMethod() == EMAIL) {
			username = request.getEmail();
		} else {
			username = request.getPhoneNumber();
		}

		var user = userRepository.getUserByPhoneNumberAndEmail(request.getPhoneNumber(), request.getEmail())
				.orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new ServiceResponseException(INVALID_CREDENTIALS_PROVIDED);
		}

		if (!user.isVerified()) {
			var alerts = serviceResponse.getAlerts();
			alerts.add(generateResponseData(String.valueOf(ACCOUNT_NOT_VERIFIED), ResponseSeverity.WARNING));
		}

		var token = jwtService.generateToken(user);

		return AuthenticationResponse.builder().token(token).userId(user.getId()).build();
	}

	public ResponseCookie generateCookie(String token) {
		return ResponseCookie.from("token", token).httpOnly(true).secure(secureCookies).path("/")
				.maxAge(Duration.ofHours(1)).sameSite("Strict").build();
	}
}
