/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import com.booker_app.backend_service.controllers.request.AppointmentRequest;
import com.booker_app.backend_service.controllers.request.AppointmentStatusRequest;
import com.booker_app.backend_service.controllers.response.dto.AppointmentDTO;
import com.booker_app.backend_service.controllers.response.dto.CustomerDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Appointment;
import com.booker_app.backend_service.models.enums.AppointmentStatus;
import com.booker_app.backend_service.models.Customer;
import com.booker_app.backend_service.models.Service;
import com.booker_app.backend_service.repositories.*;
import org.springframework.stereotype.Component;

import static com.booker_app.backend_service.controllers.response.ResponseType.*;

@Component
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final CustomerRepository customerRepository;
	private final BusinessRepository businessRepository;
	private final EmployeeRepository employeeRepository;
	private final ServicesRepository servicesRepository;

	public AppointmentService(AppointmentRepository appointmentRepository, CustomerRepository customerRepository,
			BusinessRepository businessRepository, EmployeeRepository employeeRepository,
			ServicesRepository servicesRepository) {
		this.appointmentRepository = appointmentRepository;
		this.customerRepository = customerRepository;
		this.businessRepository = businessRepository;
		this.employeeRepository = employeeRepository;
		this.servicesRepository = servicesRepository;
	}

	public UUID createAppointment(UUID companyId, UUID customerId, AppointmentRequest request) {

		if (!request.getEndTime().isAfter(request.getStartTime())) {
			throw new ServiceResponseException(INVALID_APPOINTMENT_REQUEST_TIME);
		}

		if (request.getAppointmentDate().isBefore(LocalDate.now())) {
			throw new ServiceResponseException(APPOINTMENT_DATE_IN_PAST);
		}

		var companyOpt = businessRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(BUSINESS_NOT_FOUND);
		}

		var customerOpt = customerRepository.findById(customerId);
		if (customerOpt.isEmpty()) {
			throw new ServiceResponseException(CUSTOMER_NOT_FOUND);
		}

		var appointmentsByDateOpt = appointmentRepository.getAppointmentsByDate(companyId,
				request.getAppointmentDate());

		if (appointmentsByDateOpt.isPresent()) {
			var appointmentsInTimeSlot = appointmentsByDateOpt.get().stream()
					.filter(e -> e.getStartTime().isBefore(request.getEndTime())
							&& e.getEndTime().isAfter(request.getStartTime()))
					.toList();

			if (!appointmentsInTimeSlot.isEmpty()) {
				throw new ServiceResponseException(TIME_SLOT_TAKEN);
			}
		}

		var services = servicesRepository.getServicesByName(companyId,
				request.getServices().stream().map(String::toUpperCase).toList());

		var newAppointment = Appointment.builder().services(services).startTime(request.getStartTime())
				.endTime(request.getEndTime()).appointmentDate(request.getAppointmentDate()).business(companyOpt.get())
				.customer(customerOpt.get()).build();

		appointmentRepository.save(newAppointment);
		return newAppointment.getId();
	}

	public List<AppointmentDTO> getAppointmentsByAppointmentDate(UUID companyId, LocalDate appointmentDate) {
		var companyOpt = businessRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(BUSINESS_NOT_FOUND);
		}

		if (appointmentDate.isBefore(LocalDate.now())) {
			throw new ServiceResponseException(APPOINTMENT_DATE_IN_PAST);
		}

		var appointmentsByDateOpt = appointmentRepository.getAppointmentsByDate(companyId, appointmentDate);
		if (appointmentsByDateOpt.isEmpty()) {
			return Collections.emptyList();
		}

		var appointments = appointmentsByDateOpt.get();
		return appointments.stream().map(e -> convertAppointmentToDTO(e)).toList();

	}

	public Boolean updateAppointment(UUID companyId, UUID appointmentId, UUID customerId, AppointmentRequest request) {
		var appointment = getAppointmentFromDatabase(appointmentId);

		var companyOpt = businessRepository.findById(companyId);
		if (companyOpt.isEmpty()) {
			throw new ServiceResponseException(BUSINESS_NOT_FOUND);
		}

		var appointmentsByDateOpt = appointmentRepository.getAppointmentsByDate(companyId,
				request.getAppointmentDate());

		if (appointmentsByDateOpt.isPresent()) {
			var appointmentsInTimeSlot = appointmentsByDateOpt.get().stream()
					.filter(e -> e.getStartTime().isBefore(request.getEndTime())
							&& e.getEndTime().isAfter(request.getStartTime()))
					.filter(e -> !e.getCustomer().getGeneratedId().equals(customerId)).toList();

			if (!appointmentsInTimeSlot.isEmpty()) {
				throw new ServiceResponseException(TIME_SLOT_TAKEN);
			}
		}

		var services = servicesRepository.getServicesByName(companyId,
				request.getServices().stream().map(String::toUpperCase).toList());

		appointment.setServices(services);
		appointment.setStartTime(request.getStartTime());
		appointment.setEndTime(request.getEndTime());
		appointment.setAppointmentDate(request.getAppointmentDate());
		appointmentRepository.save(appointment);
		return true;
	}

	public AppointmentStatus updateAppointmentStatus(UUID appointmentId, AppointmentStatusRequest request) {
		var appointment = getAppointmentFromDatabase(appointmentId);
		appointment.setAppointmentStatus(request.getStatus());

		appointmentRepository.save(appointment);
		return request.getStatus();
	}

	public Boolean assignAppointmentToEmployee(UUID appointmentId, UUID employeeId) {
		var appointment = getAppointmentFromDatabase(appointmentId);
		var employeeOpt = employeeRepository.findById(employeeId);
		if (employeeOpt.isEmpty()) {
			throw new ServiceResponseException(EMPLOYEE_NOT_FOUND);
		}

		// ToDo: check availability of employee
		appointment.setAssignee(employeeOpt.get());
		appointmentRepository.save(appointment);
		return true;
	}

	private Appointment getAppointmentFromDatabase(UUID id) {
		var appointmentOpt = appointmentRepository.findById(id);
		if (appointmentOpt.isEmpty()) {
			throw new ServiceResponseException(APPOINTMENT_NOT_FOUND);
		}

		return appointmentOpt.get();
	}

	public static AppointmentDTO convertAppointmentToDTO(Appointment appointment) {
		if (Objects.isNull(appointment)) {
			return null;
		}

		var services = appointment.getServices();
		var appointmentCost = BigDecimal.ZERO;
		List<String> serviceList;

		if (Objects.isNull(services) || services.isEmpty()) {
			serviceList = Collections.emptyList();
		} else {
			serviceList = services.stream().map(Service::getName).map(String::toUpperCase).toList();
			appointmentCost = services.stream().map(Service::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		String assignee = "UNASSIGNED";
		if (!Objects.isNull(appointment.getAssignee())) {
			assignee = appointment.getAssignee().getUser().getFullName();
		}

		return AppointmentDTO.builder().assignedTo(assignee).appointmentId(appointment.getId())
				.appointmentCost(appointmentCost.setScale(2, RoundingMode.HALF_UP))
				.appointmentStatus(appointment.getAppointmentStatus()).appointmentDate(appointment.getAppointmentDate())
				.customer(convertCustomerToDTO(appointment.getCustomer())).startTime(appointment.getStartTime())
				.endTime(appointment.getEndTime()).services(serviceList).build();
	}

	private static CustomerDTO convertCustomerToDTO(Customer customer) {
		var user = customer.getUser();
		return CustomerDTO.builder().dateOfBirth(user.getDateOfBirth()).fullName(user.getFullName())
				.email(user.getEmail()).phoneNumber(user.getPhoneNumber())
				.customerId(customer.getGeneratedId().toString()).build();
	}

	private List<String> formatServices(List<String> services) {
		return services.stream().map(String::toUpperCase).toList();
	}
}
