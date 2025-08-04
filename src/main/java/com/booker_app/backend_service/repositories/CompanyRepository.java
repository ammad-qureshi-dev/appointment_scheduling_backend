package com.booker_app.backend_service.repositories;

import com.booker_app.backend_service.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Query("select C from Company C where lower(C.name) = :name")
    Optional<Company> getCompanyByName(@Param("name") String name);

    @Query("select C from Company C where C.companyShortId = :shortId")
    Optional<Company> getCompanyByCompanyShortId(@Param("shortId") Long shortId);

}
