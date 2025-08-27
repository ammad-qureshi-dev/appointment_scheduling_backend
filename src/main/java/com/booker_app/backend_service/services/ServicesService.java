/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.booker_app.backend_service.controllers.request.ServiceRequest;
import com.booker_app.backend_service.controllers.request.SimpleServiceRequest;
import com.booker_app.backend_service.controllers.response.dto.ServiceDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Service;
import com.booker_app.backend_service.repositories.BusinessRepository;
import com.booker_app.backend_service.repositories.ServicesRepository;
import org.springframework.stereotype.Component;

import static com.booker_app.backend_service.controllers.response.ResponseType.BUSINESS_NOT_FOUND;
import static com.booker_app.backend_service.controllers.response.ResponseType.SERVICE_ALREADY_EXISTS;

@Component
public class ServicesService {

	private final BusinessRepository businessRepository;
	private final ServicesRepository servicesRepository;

	public ServicesService(BusinessRepository businessRepository, ServicesRepository servicesRepository) {
		this.businessRepository = businessRepository;
		this.servicesRepository = servicesRepository;
	}

	public void addService(UUID companyId, List<ServiceRequest> request) {
		var companyOpt = businessRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(BUSINESS_NOT_FOUND);
		}

		var company = companyOpt.get();
		var services = company.getServices();

		var currentServicesInStore = services.stream().map(Service::getName).map(String::toUpperCase)
				.collect(Collectors.toSet());

		for (var service : request) {
			if (currentServicesInStore.contains(service.getName().toUpperCase())) {
				throw new ServiceResponseException(SERVICE_ALREADY_EXISTS);
			}

			var newService = Service.builder().name(service.getName().toUpperCase())
					.description(service.getDescription()).price(service.getPrice()).time(service.getTime())
					.serviceLength(service.getServiceLength()).business(company).build();

			servicesRepository.save(newService);
		}
	}

	public List<ServiceDTO> getAllServices(UUID companyId) {
		var companyOpt = businessRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(BUSINESS_NOT_FOUND);
		}

		var company = companyOpt.get();
		return company.getServices().stream().map(this::convertServiceClassToDTO).toList();
	}

	public Boolean removeService(UUID companyId, SimpleServiceRequest request) {
		var companyOpt = businessRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(BUSINESS_NOT_FOUND);
		}

		var company = companyOpt.get();
		var services = company.getServices();
		for (var service : services) {
			if (request.getName().equalsIgnoreCase(service.getName())) {
				service.setBusiness(null);
				servicesRepository.save(service);
				businessRepository.save(company);
				return true;
			}
		}

		return false;
	}

	public Boolean updateService(UUID companyId, ServiceRequest request) {
		var companyOpt = businessRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(BUSINESS_NOT_FOUND);
		}

		var company = companyOpt.get();
		var services = company.getServices();
		for (var service : services) {
			if (request.getName().equalsIgnoreCase(service.getName())) {
				service.setServiceLength(request.getServiceLength());
				service.setDescription(request.getDescription());
				service.setName(request.getName());
				service.setPrice(request.getPrice());
				service.setTime(request.getTime());
				servicesRepository.save(service);
				return true;
			}
		}

		return false;
	}

	private ServiceDTO convertServiceClassToDTO(Service s) {
		return ServiceDTO.builder().name(s.getName()).description(s.getDescription()).price(s.getPrice())
				.time(s.getTime()).serviceLength(s.getServiceLength()).build();
	}

}
