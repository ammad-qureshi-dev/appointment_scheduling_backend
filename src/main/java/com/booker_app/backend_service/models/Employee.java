/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.models;

import java.util.UUID;

import com.booker_app.backend_service.models.enums.EmploymentRole;
import com.booker_app.backend_service.models.enums.OperationLevel;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {

	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID generatedId;

	@Enumerated(EnumType.STRING)
	private EmploymentRole role;

	@Enumerated(EnumType.STRING)
	private OperationLevel maxOperatingLevel;

	// Mappings
	@OneToOne
	@ToString.Exclude
	private User user;

	@ManyToOne
	@ToString.Exclude
	private Business business;
}
