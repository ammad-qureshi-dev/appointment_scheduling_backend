/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.models.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

	@Query("select C from Business C where lower(C.name) = :name")
	Optional<Business> getBusinessByName(@Param("name") String name);

	@Query(value = """
			select
				C.name as label,
				'OWNER' as role,
				E.generated_id as contextId
			from booker_app.Business C
			inner join booker_app.Employee E
			on C.owner_generated_id = E.generated_id
			inner join booker_app.User U
			on U.id = E.user_id
			where U.id = :userId
			""", nativeQuery = true)
	Optional<List<UserProfileDTO>> getBusinessProfiles(@Param("userId") UUID userId);

}
