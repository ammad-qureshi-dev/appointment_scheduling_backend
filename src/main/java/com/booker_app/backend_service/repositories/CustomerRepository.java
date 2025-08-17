/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.Optional;
import java.util.UUID;

import com.booker_app.backend_service.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

	@Query("select C from Customer C where C.company.id = :companyId and (C.user.phoneNumber = :phoneNumber or C.user.email = :email)")
	Optional<Customer> getCustomerByPhoneOrEmail(@Param("companyId") UUID companyId,
			@Param("phoneNumber") String phoneNumber, @Param("email") String email);

}
