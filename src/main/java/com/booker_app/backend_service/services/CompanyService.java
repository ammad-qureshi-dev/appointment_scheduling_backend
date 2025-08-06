/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.UUID;

import com.booker_app.backend_service.controllers.request.NewCompanyRequest;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Company;
import com.booker_app.backend_service.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseType.COMPANY_ALREADY_EXISTS;

@Service
public class CompanyService {

	private final CompanyRepository companyRepository;

	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	public UUID createNewCompany(NewCompanyRequest request) {
		var result = companyRepository.getCompanyByName(request.getName().toLowerCase());

		if (result.isPresent()) {
			throw new ServiceResponseException(COMPANY_ALREADY_EXISTS);
		}

		var newCompany = Company.builder().name(request.getName()).description(request.getDescription())
				.address(request.getAddress()).build();

		companyRepository.save(newCompany);

		return newCompany.getId();
	}

}
