/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

	@Query("select C from Company C where lower(C.name) = :name")
	Optional<Company> getCompanyByName(@Param("name") String name);

	@Query(value = """
			select
				C.name as label,
				'OWNER' as role,
				E.generated_id as contextId
			from booker_app.Company C
			inner join booker_app.Employee E
			on C.owner_generated_id = E.generated_id
			inner join booker_app.User U
			on U.id = E.user_id
			where U.id = :userId
			""", nativeQuery = true)
	Optional<List<UserProfileDTO>> getCompanyProfiles(@Param("userId") UUID userId);

}
