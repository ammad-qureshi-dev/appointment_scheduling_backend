/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.UUID;

import com.booker_app.backend_service.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Service, UUID> {
}
