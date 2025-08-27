/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.dto.EmployeeDTO;
import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

	@Query("select E from Employee E where E.user.email = :email and E.business.id = :businessId")
	Optional<Employee> getEmployeeByEmail(@Param("email") String email, @Param("businessId") UUID businessId);

	@Query("""
			select new com.booker_app.backend_service.controllers.response.dto.EmployeeDTO(
			   E.role, E.user.fullName, E.user.phoneNumber, E.user.email, E.user.dateOfBirth, E.generatedId
			)
			from Employee E where E.business.id = :businessId""")
	Optional<List<EmployeeDTO>> getAllEmployees(@Param("businessId") UUID businessId);

	@Query(value = """
			select
				C.name as label,
				'EMPLOYEE' as role,
				E.generated_id as contextId
			from booker_app.Business C
			inner join booker_app.Employee E
			on C.id = E.business_id
			inner join booker_app.User U
			on U.id = E.user_id
			where U.id = :userId
			and E.generated_id != C.owner_generated_id
			""", nativeQuery = true)

	Optional<List<UserProfileDTO>> getEmployeeProfiles(@Param("userId") UUID userId);
}
