/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.UUID;

import com.booker_app.backend_service.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
