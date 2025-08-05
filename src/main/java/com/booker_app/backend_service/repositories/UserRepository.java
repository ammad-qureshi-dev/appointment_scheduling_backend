/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.Optional;
import java.util.UUID;

import com.booker_app.backend_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	@Query("select U from User U where U.email = :email")
	Optional<User> getUserByEmail(@Param("email") String email);

	@Query("select U from User U where U.phoneNumber = :phoneNumber")
	Optional<User> getUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);

	@Query("select U from User U where U.phoneNumber = :phoneNumber or U.email = :email")
	Optional<User> getUserByPhoneNumberAndEmail(@Param("phoneNumber") String phoneNumber, @Param("email") String email);
}
