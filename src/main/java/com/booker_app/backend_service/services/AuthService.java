/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.LoginRequest;
import com.booker_app.backend_service.controllers.request.RegistrationRequest;
import com.booker_app.backend_service.controllers.response.AuthenticationResponse;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.models.enums.AccountVerificationMethod;
import com.booker_app.backend_service.models.enums.OperationLevel;
import com.booker_app.backend_service.models.enums.UserRole;
import com.booker_app.backend_service.repositories.BusinessRepository;
import com.booker_app.backend_service.repositories.CustomerRepository;
import com.booker_app.backend_service.repositories.EmployeeRepository;
import com.booker_app.backend_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.booker_app.backend_service.controllers.response.ResponseType.*;
import static com.booker_app.backend_service.models.enums.LoginMethod.EMAIL;
import static com.booker_app.backend_service.models.enums.LoginMethod.PHONE;
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

	@Value("${booking-service.secureCookies}")
	private boolean secureCookies;

	public UUID registerUser(RegistrationRequest request, HttpServletResponse response) {
		var userResult = userRepository.getUserByEmail(request.getEmail());
		if (userResult.isPresent()) {
			throw new ServiceResponseException(USER_ALREADY_EXISTS);
		}

		var newUser = User.builder().email(request.getEmail()).password(request.getPassword())
				.dateOfBirth(request.getDateOfBirth()).fullName(request.getFullName())
				.phoneNumber(request.getPhoneNumber()).build();

		userRepository.save(newUser);

		// ToDo: send verification email

		return newUser.getId();
	}

	public UUID userLogin(LoginRequest loginRequest, HttpServletResponse response) {
		var user = userRepository.getUserByPhoneNumberAndEmail(loginRequest.getPhoneNumber(), loginRequest.getEmail())
				.orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

		if (EMAIL.equals(loginRequest.getLoginMethod())) {
			if (Objects.isNull(user.getPassword())) {
				throw new ServiceResponseException(ACCOUNT_NOT_COMPLETED);
			} else if (!user.getPassword().equals(loginRequest.getPassword())) {
				throw new ServiceResponseException(INVALID_CREDENTIALS_PROVIDED);
			} else if (!user.isVerified()) {
				// ToDo: send verification email
				throw new ServiceResponseException(ACCOUNT_NOT_VERIFIED);
			}
		} else {
			// ToDo: add OTP here
			throw new ServiceResponseException(NOT_IMPLEMENTED_YET);
		}

		return user.getId();
	}

	private OperationLevel getEmployeeOperationalLevel(UUID employeeId) {
		var employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ServiceResponseException(EMPLOYEE_NOT_FOUND));
		return employee.getMaxOperatingLevel();
	}

	public void switchUserRole(UUID userId, HttpServletResponse response, UserRole role) {
		OperationLevel maxOperationLevel;

		// ToDo: add verification if userId exists within the contexts like for employee
		switch (role) {
			case OWNER -> maxOperationLevel = OperationLevel.DELETE;
			case CUSTOMER -> maxOperationLevel = OperationLevel.NONE;
			case EMPLOYEE -> maxOperationLevel = getEmployeeOperationalLevel(userId);
			default -> throw new RuntimeException("Role not supported");
		}

		var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));
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

	public boolean verifyAccount(UUID userId, AccountVerificationMethod method) {
		var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));
		if (EMAIL.equals(method)) {
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

	public ResponseCookie setCookie(String token) {
		return ResponseCookie.from("token", token).httpOnly(true).secure(secureCookies).path("/")
				.maxAge(Duration.ofHours(1)).sameSite("Strict").build();
	}

}
