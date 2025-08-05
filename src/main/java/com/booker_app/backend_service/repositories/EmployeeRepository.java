/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.dto.EmployeeDTO;
import com.booker_app.backend_service.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

	@Query("select E from Employee E where E.user.email = :email and E.company.id = :companyId")
	Optional<Employee> getEmployeeByEmail(@Param("email") String email, @Param("companyId") UUID companyId);

	@Query("""
			select new com.booker_app.backend_service.controllers.response.dto.EmployeeDTO(
			   E.role, E.user.fullName, E.user.phoneNumber, E.user.email, E.user.dateOfBirth, E.generatedId
			)
			from Employee E where E.company.id = :companyId""")
	Optional<List<EmployeeDTO>> getAllEmployees(@Param("companyId") UUID companyId);
}
