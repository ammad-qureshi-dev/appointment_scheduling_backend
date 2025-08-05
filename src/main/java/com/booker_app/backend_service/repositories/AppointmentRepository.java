package com.booker_app.backend_service.repositories;

import com.booker_app.backend_service.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("""
            select count(A)
            from Appointment A
            where A.company.id = :companyId
            and A.appointmentDate = :appointmentDate
            and A.startTime < :endTime
            and A.endTime > :startTime
            and A.customer.generatedId != :customerId
            """)
    Integer findAmountOfBookingsWithinBookingTime(@Param("companyId") UUID companyId,
                                                  @Param("appointmentDate") LocalDate appointmentDate,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime,
                                                  @Param("customerId") UUID customerId);

    @Query("""
            select A
            from Appointment A
            where A.company.id = :companyId
            and A.appointmentDate = :appointmentDate
            order by A.startTime asc
            """)
    Optional<List<Appointment>> getAppointmentsByDate(@Param("companyId") UUID companyId, @Param("appointmentDate") LocalDate appointmentDate);
}
