package com.booker_app.backend_service.repositories;

import com.booker_app.backend_service.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<Service, UUID> {
}
