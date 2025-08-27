/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.UUID;

import com.booker_app.backend_service.controllers.request.NewBusinessRequest;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Business;
import com.booker_app.backend_service.repositories.BusinessRepository;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseType.BUSINESS_ALREADY_EXISTS;

@Service
public class BusinessService {

	private final BusinessRepository businessRepository;

	public BusinessService(BusinessRepository businessRepository) {
		this.businessRepository = businessRepository;
	}

	public UUID createNewBusiness(NewBusinessRequest request) {
		var result = businessRepository.getBusinessByName(request.getName().toLowerCase());

		if (result.isPresent()) {
			throw new ServiceResponseException(BUSINESS_ALREADY_EXISTS);
		}

		var newBusiness = Business.builder().name(request.getName()).description(request.getDescription())
				.address(request.getAddress()).build();

		businessRepository.save(newBusiness);

		return newBusiness.getId();
	}

}
