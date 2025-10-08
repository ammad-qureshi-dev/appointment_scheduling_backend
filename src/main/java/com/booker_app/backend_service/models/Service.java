/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.models.enums.ServiceLength;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Service extends BaseEntity {

	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	private String name;
	private String description;
	private BigDecimal price;

	// 1 HOUR, 25 MINUTES, etc
	private Integer time;
	private ServiceLength serviceLength;

	// Mappings
	@ManyToOne
	@ToString.Exclude
	private Business business;

	@ToString.Exclude
	@ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
	private List<Appointment> appointments;

}
