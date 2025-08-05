package com.booker_app.backend_service.services;

import com.booker_app.backend_service.controllers.request.CustomerRequest;
import com.booker_app.backend_service.controllers.response.dto.CustomerDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.Customer;
import com.booker_app.backend_service.models.User;
import com.booker_app.backend_service.repositories.CompanyRepository;
import com.booker_app.backend_service.repositories.CustomerRepository;
import com.booker_app.backend_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class CustomerService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public CustomerService(CompanyRepository companyRepository, UserRepository userRepository, CustomerRepository customerRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    public UUID addCustomer(UUID companyId, CustomerRequest request) {
        var companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new ServiceResponseException("Company not found");
        }

        var userOpt = userRepository.getUserByPhoneNumberAndEmail(request.getPhoneNumber(), request.getEmail());
        User newUnverifiedUser = null;
        if (userOpt.isEmpty()) {
            // Customers don't necessarily have to have an account, however, we can pre-emptively create a user for them
            // and when they do decide to register, we can let them know they have an account, just need a password
            var newUser = User.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .email(request.getEmail())
                    .isVerified(false)
                    .build();
            userRepository.save(newUser);
            newUnverifiedUser = newUser;
        }

        if (userOpt.isPresent()) {
            var existingCustomer = companyOpt.get().getCustomers().stream()
                    .map(Customer::getUser)
                    .filter(e -> request.getEmail().equalsIgnoreCase(e.getEmail()))
                    .filter(e -> request.getPhoneNumber().equalsIgnoreCase(e.getPhoneNumber()))
                    .findFirst();

            if (existingCustomer.isPresent()) {
                throw new ServiceResponseException("Customer already exists");
            }
        }

        var newCustomer = Customer.builder()
                .user(userOpt.orElse(newUnverifiedUser))
                .company(companyOpt.get())
                .build();

        customerRepository.save(newCustomer);
        return newCustomer.getGeneratedId();
    }

    public List<CustomerDTO> getAllCustomers(UUID companyId) {
        var companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new ServiceResponseException("Company not found");
        }

        var company = companyOpt.get();
        return company.getCustomers().stream().map(this::convertCustomerToDTO).toList();
    }

    public Boolean deleteCustomer(UUID companyId, CustomerRequest request) {
        var companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new ServiceResponseException("Company not found");
        }

        var company = companyOpt.get();
        var customerOpt = company.getCustomers().stream()
                .filter(e -> (Objects.nonNull(e.getUser().getPhoneNumber()) && e.getUser().getPhoneNumber().equalsIgnoreCase(request.getPhoneNumber())
                || (Objects.nonNull(e.getUser().getEmail()) && e.getUser().getEmail().equalsIgnoreCase(request.getEmail()))))
                .findFirst();

        if (customerOpt.isEmpty()) {
            log.debug("Customer with email={} and phoneNumber={} did not exist, no deletion occurred", request.getEmail(), request.getPhoneNumber());
            return false;
        }

        var customer = customerOpt.get();
        customer.setCompany(null);
        customerRepository.save(customer);
        company.getCustomers().remove(customer);
        companyRepository.save(company);
        return true;
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
}
