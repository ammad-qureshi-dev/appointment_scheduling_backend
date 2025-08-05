package com.booker_app.backend_service.controllers.request;

import com.booker_app.backend_service.models.AppointmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    @NonNull
    private List<String> services;
    @NonNull
    private LocalDateTime startTime;
    @NonNull
    private LocalDateTime endTime;
    @NonNull
    private LocalDate appointmentDate;
    @NonNull
    private AppointmentStatus status;
}
