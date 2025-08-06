/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.EmployeeRequest;
import com.booker_app.backend_service.controllers.response.dto.EmployeeDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Employee;
import com.booker_app.backend_service.models.EmploymentRole;
import com.booker_app.backend_service.repositories.CompanyRepository;
import com.booker_app.backend_service.repositories.EmployeeRepository;
import com.booker_app.backend_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.booker_app.backend_service.controllers.response.ResponseType.*;

@Slf4j
@Service
public class EmployeeService {

	private final CompanyRepository companyRepository;
	private final EmployeeRepository employeeRepository;
	private final UserRepository userRepository;

	public EmployeeService(CompanyRepository companyRepository, EmployeeRepository employeeRepository,
			UserRepository userRepository) {
		this.companyRepository = companyRepository;
		this.employeeRepository = employeeRepository;
		this.userRepository = userRepository;
	}

	// ToDo: add role logic where only OWNER can add employee
	public UUID addEmployeeToCompany(UUID companyId, EmployeeRequest request) {
		var companyResult = companyRepository.findById(companyId);
		if (companyResult.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		var userResult = userRepository.getUserByEmail(request.getEmail());
		if (userResult.isEmpty()) {
			throw new ServiceResponseException(UNREGISTERED_USER_EMAIL);
		}

		var company = companyResult.get();
		for (var employee : company.getEmployees()) {
			if (request.getEmail().equals(employee.getUser().getEmail())) {
				throw new ServiceResponseException(EMPLOYEE_EMAIL_ALREADY_EXISTS);
			}
		}

		var newEmployee = Employee.builder().company(company).role(request.getRole()).user(userResult.get()).build();

		if (EmploymentRole.OWNER.equals(request.getRole())) {
			company.setOwner(newEmployee);
		}

		company.getEmployees().add(newEmployee);

		companyRepository.save(company);

		return company.getEmployees().getLast().getGeneratedId();
	}

	public List<EmployeeDTO> getAllEmployees(UUID companyId) {
		var companyResult = companyRepository.findById(companyId);
		if (companyResult.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		var result = employeeRepository.getAllEmployees(companyId);
		return result.orElse(Collections.emptyList());
	}

	// ToDo: add role logic where only OWNER can remove employee
	public Boolean removeEmployeeFromCompany(UUID companyId, EmployeeRequest request) {
		var companyResult = companyRepository.findById(companyId);
		if (companyResult.isEmpty()) {
			throw new ServiceResponseException(COMPANY_NOT_FOUND);
		}

		var userOpt = userRepository.getUserByEmail(request.getEmail());
		if (userOpt.isEmpty()) {
			throw new ServiceResponseException(USER_NOT_FOUND);
		}

		var company = companyResult.get();
		var employeeOpt = company.getEmployees().stream()
				.filter(e -> request.getEmail().equalsIgnoreCase(e.getUser().getEmail())).findFirst();

		if (employeeOpt.isEmpty()) {
			log.debug("employee={} didn't exist initially, list not modified", request.getEmail());
			return false;
		}

		var employee = employeeOpt.get();

		if (EmploymentRole.OWNER.equals(employee.getRole())) {
			throw new ServiceResponseException(CANNOT_REMOVE_OWNER);
		}

		employee.setCompany(null);
		employeeRepository.delete(employee);

		var employeeListSize = company.getEmployees().size();
		company.getEmployees().remove(employee);
		companyRepository.save(company);

		return company.getEmployees().size() < employeeListSize;
	}
}
