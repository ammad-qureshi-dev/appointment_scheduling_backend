/* (C) 2025 
Business Booking App. */
package com.booker_app.backend_service.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceResponse<T> implements Serializable {
	private boolean isSuccess;
	private transient T data;
	private UUID requestId;
	private LocalDateTime requestCompletedAt;

	@Builder.Default
	private List<ResponseData> alerts = new ArrayList<>();

	public static <T> ResponseEntity<ServiceResponse<T>> getServiceResponse(boolean isSuccess, T data,
			HttpStatus httpStatus) {
		var serviceResponse = ServiceResponse.<T>builder().isSuccess(isSuccess).data(data).requestId(UUID.randomUUID())
				.requestCompletedAt(LocalDateTime.now()).build();

		return new ResponseEntity<>(serviceResponse, httpStatus);
	}

	public static <T> ResponseEntity<ServiceResponse<T>> getServiceResponse(boolean isSuccess, T data,
			HttpStatus httpStatus, List<ResponseData> alerts) {
		var serviceResponse = getServiceResponse(isSuccess, data, httpStatus);

		var serviceResponseBody = serviceResponse.getBody();

		if (Objects.isNull(serviceResponseBody)) {
			throw new RuntimeException("serviceResponseBody is empty");
		}

		// Add errors to response
		serviceResponseBody.setAlerts(alerts);

		return new ResponseEntity<>(serviceResponse.getBody(), httpStatus);
	}

}
