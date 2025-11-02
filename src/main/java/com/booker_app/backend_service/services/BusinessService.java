/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.UUID;

import com.booker_app.backend_service.controllers.request.NewBusinessRequest;
import com.booker_app.backend_service.controllers.response.dto.BusinessDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Business;
import com.booker_app.backend_service.models.Employee;
import com.booker_app.backend_service.repositories.BusinessRepository;
import com.booker_app.backend_service.repositories.EmployeeRepository;
import com.booker_app.backend_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseType.*;

@Service
@RequiredArgsConstructor
public class BusinessService {

	private final BusinessRepository businessRepository;
	private final EmployeeRepository employeeRepository;
	private final UserRepository userRepository;

	public BusinessDTO findBusinessById(UUID businessId) {
		return businessRepository.findBusinessById(businessId)
				.orElseThrow(() -> new ServiceResponseException(BUSINESS_NOT_FOUND));
	}

	@Transactional
	public UUID createNewBusiness(NewBusinessRequest request, UUID userId) {
		validateBusinessName(request.getName());

		var newBusiness = Business.builder().name(request.getName()).email(request.getEmail())
				.phoneNumber(request.getPhoneNumber()).description(request.getDescription())
				.address(request.getAddress()).build();

		businessRepository.save(newBusiness);

		var businessId = newBusiness.getId();

		assignOwnerToBusiness(businessId, userId);

		return businessId;
	}

	@Transactional
	public void assignOwnerToBusiness(UUID businessId, UUID userId) {
		var user = userRepository.findById(userId).orElseThrow(() -> new ServiceResponseException(USER_NOT_FOUND));
		var business = businessRepository.findById(businessId)
				.orElseThrow(() -> new ServiceResponseException(BUSINESS_NOT_FOUND));

		var ownerResult = employeeRepository.findByUserId(userId);
		Employee owner = null;

		owner = ownerResult.orElseGet(() -> Employee.builder().user(user).business(business).build());

		employeeRepository.save(owner);

		business.setOwner(owner);

		businessRepository.save(business);
	}

	private void validateBusinessName(String businessName) {
		var result = businessRepository.getBusinessByName(businessName.toLowerCase());
		if (result.isPresent()) {
			throw new ServiceResponseException(BUSINESS_ALREADY_EXISTS);
		}
	}

}
