/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import com.booker_app.backend_service.controllers.request.CustomerRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.CustomerDTO;
import com.booker_app.backend_service.controllers.response.dto.CustomerOverviewDTO;
import com.booker_app.backend_service.controllers.response.dto.ServiceOverviewDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Appointment;
import com.booker_app.backend_service.models.Customer;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.booker_app.backend_service.controllers.response.ResponseType.*;

@Slf4j
@Component
public class CustomerService {

	private final ServiceResponse<?> serviceResponse;

	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final ServicesRepository servicesRepository;
	private final AppointmentRepository appointmentRepository;

	public CustomerService(ServiceResponse<?> serviceResponse, CompanyRepository companyRepository,
			UserRepository userRepository, CustomerRepository customerRepository, ServicesRepository servicesRepository,
			AppointmentRepository appointmentRepository) {
		this.serviceResponse = serviceResponse;
		this.companyRepository = companyRepository;
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.servicesRepository = servicesRepository;
		this.appointmentRepository = appointmentRepository;
	}

	public UUID addCustomer(UUID companyId, CustomerRequest request) {
		var companyOpt = companyRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		var company = companyOpt.get();
		for (var customer : company.getCustomers()) {
			var user = customer.getUser();
			if (isUserAlreadyExists(user, request)) {
				throw new ServiceResponseException(CUSTOMER_ALREADY_EXISTS);
			}
		}

		Customer newCustomer = null;
		var storedUser = userRepository.getUserByPhoneNumberAndEmail(request.getPhoneNumber(), request.getEmail());
		if (storedUser.isPresent()) {
			newCustomer = Customer.builder().user(storedUser.get()).company(companyOpt.get()).build();
		} else {
			var newUser = User.builder().phoneNumber(request.getPhoneNumber()).email(request.getEmail())
					.fullName(request.getFullName()).isVerified(false).build();

			userRepository.save(newUser);
			newCustomer = Customer.builder().user(newUser).company(companyOpt.get()).build();
		}

		customerRepository.save(newCustomer);
		return newCustomer.getGeneratedId();
	}

	public CustomerDTO getCustomerByPhoneOrEmail(UUID companyId, CustomerRequest searchRequest) {
		companyRepository.findById(companyId).orElseThrow(() -> new ServiceResponseException(COMPANY_NOT_FOUND));
		var customer = customerRepository.getCustomerByPhoneOrEmail(companyId, searchRequest.getPhoneNumber(),
				searchRequest.getEmail());
		return customer.map(this::convertCustomerToDTO).orElse(null);
	}

	public List<CustomerDTO> getAllCustomers(UUID companyId) {
		var companyOpt = companyRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		var company = companyOpt.get();
		return company.getCustomers().stream().map(this::convertCustomerToDTO).toList();
	}

	public Boolean deleteCustomer(UUID companyId, CustomerRequest request) {
		var companyOpt = companyRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		var company = companyOpt.get();
		var customerOpt = company.getCustomers().stream()
				.filter(e -> (Objects.nonNull(e.getUser().getPhoneNumber())
						&& e.getUser().getPhoneNumber().equalsIgnoreCase(request.getPhoneNumber())
						|| (Objects.nonNull(e.getUser().getEmail())
								&& e.getUser().getEmail().equalsIgnoreCase(request.getEmail()))))
				.findFirst();

		if (customerOpt.isEmpty()) {
			var msg = String.format("Customer with email=%s and phoneNumber=%s not found", request.getEmail(),
					request.getPhoneNumber());
			serviceResponse.getAlerts().add(ResponseData.builder().responseSeverity(ResponseSeverity.WARNING)
					.responseType(CUSTOMER_NOT_FOUND).description(msg).build());
			return false;
		}

		var customer = customerOpt.get();
		customer.setCompany(null);
		customerRepository.save(customer);
		company.getCustomers().remove(customer);
		companyRepository.save(company);
		return true;
	}

	// ToDo: speed performance and reduce ORM overhead
	public CustomerOverviewDTO getCustomerAppointmentOverview(UUID customerId, UUID companyId) {
		var companyOpt = companyRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		var customerOpt = customerRepository.findById(customerId);
		if (customerOpt.isEmpty()) {
			throw new ServiceResponseException(CUSTOMER_NOT_FOUND);
		}

		var serviceOverview = servicesRepository.getAppointmentServiceOverview(companyId, customerId);
		var upcomingAppointmentOpt = appointmentRepository.getLatestAppointment(companyId, customerId);

		var totalSpend = BigDecimal.ZERO;
		var avgSpend = BigDecimal.ZERO;
		if (!Objects.isNull(serviceOverview) && !serviceOverview.isEmpty()) {
			totalSpend = serviceOverview.stream().map(ServiceOverviewDTO::getTotal)
					.reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
			avgSpend = totalSpend.divide(
					BigDecimal.valueOf(
							serviceOverview.stream().map(ServiceOverviewDTO::getNumberOfTimes).reduce(0L, Long::sum)),
					2, RoundingMode.HALF_UP);
		}

		Appointment upcomingAppointment = upcomingAppointmentOpt.orElse(null);

		return CustomerOverviewDTO.builder().serviceOverview(serviceOverview)
				.customerId(customerOpt.get().getGeneratedId().toString())
				.customerSince(customerOpt.get().getCreatedAt().toLocalDate()).totalSpend(totalSpend).avgSpend(avgSpend)
				.upcomingAppointment(AppointmentService.convertAppointmentToDTO(upcomingAppointment)).build();
	}

	public List<CustomerDTO> getCustomerBySearch(UUID companyId, String searchParam) {
		var companyOpt = companyRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		return customerRepository.findCustomerBySearch(companyId, searchParam);
	}

	private CustomerDTO convertCustomerToDTO(Customer customer) {
		if (Objects.isNull(customer)) {
			return null;
		}
		var user = customer.getUser();
		return CustomerDTO.builder().dateOfBirth(user.getDateOfBirth()).fullName(user.getFullName())
				.email(user.getEmail()).phoneNumber(user.getPhoneNumber())
				.customerId(customer.getGeneratedId().toString()).build();
	}

	private static boolean isUserAlreadyExists(User user, CustomerRequest request) {
		return (StringUtils.hasLength(request.getPhoneNumber())
				&& request.getPhoneNumber().equalsIgnoreCase(user.getPhoneNumber()))
				|| (StringUtils.hasLength(request.getEmail()) && request.getEmail().equalsIgnoreCase(user.getEmail()));
	}

}
