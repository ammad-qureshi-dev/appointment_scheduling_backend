/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.models.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointment", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseEntity {

	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private LocalDate appointmentDate;

	@Builder.Default
	@Enumerated(value = EnumType.STRING)
	private AppointmentStatus appointmentStatus = AppointmentStatus.PENDING;

	// Mappings
	@ManyToOne
	@ToString.Exclude
	private Business business;

	@ManyToOne
	@ToString.Exclude
	private Customer customer;

	@OneToOne
	private Employee assignee;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "appointment_services", schema = "booker_app", joinColumns = @JoinColumn(name = "appointment_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
	private List<Service> services = new ArrayList<>();

}
