package com.booker_app.backend_service.services;

import com.booker_app.backend_service.configs.JwtConfiguration;
import com.booker_app.backend_service.controllers.request.LoginRequest;
import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.UserRole;
import com.booker_app.backend_service.repositories.CompanyRepository;
import com.booker_app.backend_service.repositories.CustomerRepository;
import com.booker_app.backend_service.repositories.EmployeeRepository;
import com.booker_app.backend_service.repositories.UserRepository;
import com.booker_app.backend_service.utils.HttpUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.booker_app.backend_service.controllers.response.ResponseType.INVALID_CREDENTIALS_PROVIDED;
import static com.booker_app.backend_service.controllers.response.ResponseType.USER_NOT_FOUND;
import static com.booker_app.backend_service.utils.Constants.Auth.TOKEN;

@Component
public class AuthService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final JwtConfiguration jwtConfiguration;

    public AuthService(CompanyRepository companyRepository, EmployeeRepository employeeRepository, CustomerRepository customerRepository, UserRepository userRepository, JwtConfiguration jwtConfiguration) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.jwtConfiguration = jwtConfiguration;
    }

    public UUID userLogin(LoginRequest loginRequest, HttpServletResponse response) {
        var user = userRepository.getUserByPhoneNumberAndEmail(loginRequest.getPhoneNumber(), loginRequest.getEmail()).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

        if (loginRequest.isLoginByEmail()) {
            if (!user.getPassword().equals(loginRequest.getPassword())) {
                throw new ServiceResponseException(INVALID_CREDENTIALS_PROVIDED);
            }

            var token = jwtConfiguration.generateToken(user);
            HttpUtil.addCookie(response, TOKEN, token);
            return user.getId();
        } else {
            // ToDo: add OTP here
            throw new ServiceResponseException("OTP Not Implemented Yet");
        }
    }

    public void switchUserRole(UUID userId, HttpServletResponse response, UserRole role) {
        var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));

        // Don't save, only for token generation
        user.setUserRole(role);

        var token = jwtConfiguration.generateToken(user);
        HttpUtil.addCookie(response, TOKEN, token);
    }

    public List<UserProfileDTO> getUserProfiles(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow();
        var profiles = new ArrayList<UserProfileDTO>();
        profiles.add(
                UserProfileDTO.builder()
                        .label(user.getFullName())
                        .contextId(userId)
                        .role(UserRole.CUSTOMER.toString())
                        .build()
        );

        profiles.addAll(companyRepository.getCompanyProfiles(userId).orElseGet(ArrayList::new));
        profiles.addAll(employeeRepository.getEmployeeProfiles(userId).orElseGet(ArrayList::new));
        return profiles;
    }
}
