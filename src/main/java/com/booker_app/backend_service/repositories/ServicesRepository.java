/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.repositories;

import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.dto.ServiceOverviewDTO;
import com.booker_app.backend_service.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicesRepository extends JpaRepository<Service, UUID> {
	@Query("""
			select s from Service s
			where s.company.id = :companyId
			and upper(s.name) in :services
			""")
	List<Service> getServicesByName(@Param("companyId") UUID companyId, @Param("services") List<String> services);

	@Query(value = """
			select ss.name as name, count(*) as numberOfTimes, ss.price as price, ss.price * count(*) as total
			from booker_app.appointment_services aass
			inner join booker_app.service ss on aass.service_id = ss.id
			inner join booker_app.appointment aa on aass.appointment_id = aa.id
			where aa.customer_generated_id = :customerId
			  and aa.company_id = :companyId
			  and aa.appointment_date < now()
			group by ss.name, ss.price
			order by total desc
			""", nativeQuery = true)
	List<ServiceOverviewDTO> getAppointmentServiceOverview(@Param("companyId") UUID companyId,
			@Param("customerId") UUID customerId);
}
