/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.dto.CustomerDTO;
import com.booker_app.backend_service.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

	@Query("select C from Customer C where C.business.id = :businessId and (C.user.phoneNumber = :phoneNumber or C.user.email = :email)")
	Optional<Customer> getCustomerByPhoneOrEmail(@Param("businessId") UUID businessId,
			@Param("phoneNumber") String phoneNumber, @Param("email") String email);

	@Query("""
			   SELECT c FROM Customer c
			   LEFT JOIN c.appointments a
			   LEFT JOIN a.services s
			   WHERE c.id = :customerId AND c.business.id = :businessId
			""")
	Optional<Customer> findWithAppointmentsAndServices(UUID customerId, UUID businessId);

	@Query(value = """
			  select
			      u.full_name as fullName,
			      u.phone_number as phoneNumber,
			      u.email as email,
			cast(u.date_of_birth as date) as dateOfBirth,
			      cast(c.generated_id as varchar) as customerId
			  from booker_app.customer c
			  inner join booker_app.user u on c.user_id = u.id
			  where c.generated_id = :customerId
			  """, nativeQuery = true)
	Optional<CustomerDTO> findCustomerById(@Param("customerId") UUID customerId);

	// ToDo: fix error when including dateOfBirth in query result
	@Query(value = """
			select
				u.full_name as fullName,
				u.phone_number as phoneNumber,
				u.email as email,
				cast(c.generated_id as varchar) as customerId
			from booker_app.Customer c
			inner join booker_app.business cc on c.business_id = cc.id
			inner join booker_app.User u on c.user_id = u.id
			where u.full_name ilike concat('%', :searchParam, '%')
			 		or u.phone_number like concat('%', :searchParam, '%')
			 		or u.email ilike concat('%', :searchParam, '%')
			order by u.full_name asc
			""", nativeQuery = true)
	List<CustomerDTO> findCustomerBySearch(@Param("businessId") UUID businessId,
			@Param("searchParam") String searchParam);

}
