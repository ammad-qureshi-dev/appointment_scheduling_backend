/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.models;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.booker_app.backend_service.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements UserDetails {

	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	private String fullName;
	private String phoneNumber;

	@Column(unique = true)
	private String email;
	private LocalDate dateOfBirth;
	private String password;
	private boolean isVerified;
	private UUID lastUsedContext;

	@Enumerated(EnumType.STRING)
	private UserRole lastSignedInAs;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (lastSignedInAs == null) {
			return List.of();
		}

		return List.of(() -> "ROLE_" + lastSignedInAs.name());
	}

	@Override
	public String getUsername() {
		if (!Objects.isNull(getEmail())) {
			return getEmail();
		}

		return getPhoneNumber();
	}
}
