package com.booker_app.backend_service.models;

import com.booker_app.backend_service.utils.StringListConverterUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "services", columnDefinition = "text[]")
    @Convert(converter = StringListConverterUtil.class)
    private List<String> services;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Mappings
    @ManyToOne
    private Company company;

    @ManyToOne
    private Customer customer;
}


