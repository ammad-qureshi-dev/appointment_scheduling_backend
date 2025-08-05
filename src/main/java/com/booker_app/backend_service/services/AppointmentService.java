package com.booker_app.backend_service.services;

import com.booker_app.backend_service.controllers.request.AppointmentRequest;
import com.booker_app.backend_service.controllers.request.AppointmentStatusRequest;
import com.booker_app.backend_service.controllers.response.dto.AppointmentDTO;
import com.booker_app.backend_service.controllers.response.dto.CustomerDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Appointment;
import com.booker_app.backend_service.models.AppointmentStatus;
import com.booker_app.backend_service.models.Customer;
import com.booker_app.backend_service.repositories.AppointmentRepository;
import com.booker_app.backend_service.repositories.CompanyRepository;
import com.booker_app.backend_service.repositories.CustomerRepository;
import com.booker_app.backend_service.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.booker_app.backend_service.controllers.response.ResponseType.COMPANY_NOT_FOUND;
import static com.booker_app.backend_service.controllers.response.ResponseType.TIME_SLOT_TAKEN;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, CustomerRepository customerRepository, CompanyRepository companyRepository, EmployeeRepository employeeRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
    }

    public UUID createAppointment(UUID companyId, UUID customerId, AppointmentRequest request) {

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new ServiceResponseException("End Time must occur after Start Time");
        }

        var companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new ServiceResponseException(COMPANY_NOT_FOUND);
        }

        var customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new ServiceResponseException("Customer does not exist");
        }

        var appointmentsByDateOpt = appointmentRepository.getAppointmentsByDate(companyId, request.getAppointmentDate());

        if (appointmentsByDateOpt.isPresent()) {
            var appointmentsInTimeSlot = appointmentsByDateOpt.get().stream()
                    .filter(e ->
                            e.getStartTime().isBefore(request.getEndTime()) &&
                                    e.getEndTime().isAfter(request.getStartTime())
                    )
                    .toList();

            if (!appointmentsInTimeSlot.isEmpty()) {
                throw new ServiceResponseException(TIME_SLOT_TAKEN);
            }
        }

        var newAppointment = Appointment.builder()
                .services(String.join(",", formatServices(request.getServices())))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .appointmentDate(request.getAppointmentDate())
                .company(companyOpt.get())
                .customer(customerOpt.get())
                .build();

        appointmentRepository.save(newAppointment);
        return newAppointment.getId();
    }

    public List<AppointmentDTO> getAppointmentsByAppointmentDate(UUID companyId, LocalDate appointmentDate) {
        var companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new ServiceResponseException(COMPANY_NOT_FOUND);
        }

        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new ServiceResponseException("Appointment Date cannot be in the past");
        }

        var appointmentsByDateOpt = appointmentRepository.getAppointmentsByDate(companyId, appointmentDate);
        if (appointmentsByDateOpt.isEmpty()) {
            return Collections.emptyList();
        }

        var appointments = appointmentsByDateOpt.get();
        return appointments.stream().map(this::convertAppointmentToDTO).toList();

    }

    public Boolean updateAppointment(UUID companyId, UUID appointmentId, UUID customerId, AppointmentRequest request) {
        var appointment = getAppointmentFromDatabase(appointmentId);

        var companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new ServiceResponseException(COMPANY_NOT_FOUND);
        }

        var appointmentsByDateOpt = appointmentRepository.getAppointmentsByDate(companyId, request.getAppointmentDate());

        if (appointmentsByDateOpt.isPresent()) {
            var appointmentsInTimeSlot = appointmentsByDateOpt.get().stream()
                    .filter(e ->
                            e.getStartTime().isBefore(request.getEndTime()) &&
                                    e.getEndTime().isAfter(request.getStartTime())
                    ).filter(e -> !e.getCustomer().getGeneratedId().equals(customerId))
                    .toList();

            if (!appointmentsInTimeSlot.isEmpty()) {
                throw new ServiceResponseException(TIME_SLOT_TAKEN);
            }
        }

        appointment.setServices(String.join(",", formatServices(request.getServices())));
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointmentRepository.save(appointment);
        return true;
    }

    public AppointmentStatus updateAppointmentStatus (UUID appointmentId, AppointmentStatusRequest request) {
        var appointment = getAppointmentFromDatabase(appointmentId);
        appointment.setAppointmentStatus(request.getStatus());

        appointmentRepository.save(appointment);
        return request.getStatus();
    }

    public Boolean assignAppointmentToEmployee(UUID appointmentId, UUID employeeId) {
        var appointment = getAppointmentFromDatabase(appointmentId);
        var employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ServiceResponseException("Employee not found");
        }

        // ToDo: check availability of employee
        appointment.setAssignee(employeeOpt.get());
        appointmentRepository.save(appointment);
        return true;
    }

    private Appointment getAppointmentFromDatabase(UUID id) {
        var appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            throw new ServiceResponseException("Appointment not found");
        }

        return appointmentOpt.get();
    }

    private AppointmentDTO convertAppointmentToDTO(Appointment appointment) {
        var services = appointment.getServices();
        List<String> serviceList;

        if (Objects.isNull(services) || services.isEmpty()) {
            serviceList = Collections.emptyList();
        } else {
            serviceList = List.of(String.join(",", services));
        }

        String assignee = "UNASSIGNED";
        if (!Objects.isNull(appointment.getAssignee())) {
            assignee = appointment.getAssignee().getUser().getFullName();
        }

        return AppointmentDTO.builder()
                .assignedTo(assignee)
                .appointmentId(appointment.getId())
                .appointmentStatus(appointment.getAppointmentStatus())
                .appointmentDate(appointment.getAppointmentDate())
                .customer(convertCustomerToDTO(appointment.getCustomer()))
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .services(serviceList)
                .build();
    }

    private CustomerDTO convertCustomerToDTO(Customer customer) {
        var user = customer.getUser();
        return CustomerDTO.builder()
                .dateOfBirth(user.getDateOfBirth())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .customerId(customer.getGeneratedId())
                .build();
    }

    private List<String> formatServices(List<String> services) {
        return services.stream().map(String::toUpperCase).toList();
    }
}
