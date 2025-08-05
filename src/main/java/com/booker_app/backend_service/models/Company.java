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
@Table(name = "company", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Company extends BaseEntity {

	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	private String name;
	private String phoneNumber;
	private String email;
	private String description;
	private String address;

	// Mappings
	@ToString.Exclude
	@OneToOne
	private Employee owner;

	@ToString.Exclude
	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Employee> employees;

	@ToString.Exclude
	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Appointment> appointments;

	@ToString.Exclude
	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Customer> customers;

	@ToString.Exclude
	@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Service> services;

}
