/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.models;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID generatedId;

	// Mappings
	@OneToOne
	@ToString.Exclude
	private User user;

	@ManyToOne
	@ToString.Exclude
	private Company company;

	@ToString.Exclude
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Appointment> appointments;
}
