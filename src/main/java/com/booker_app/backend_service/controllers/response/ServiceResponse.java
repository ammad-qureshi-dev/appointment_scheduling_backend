/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
			HttpStatus httpStatus, HttpHeaders headers, List<ResponseData> alerts) {

		var serviceResponse = ServiceResponse.<T>builder().isSuccess(isSuccess).data(data).requestId(UUID.randomUUID())
				.requestCompletedAt(LocalDateTime.now()).alerts(alerts).build();

		return new ResponseEntity<>(serviceResponse, headers, httpStatus);
	}

	public static <T> ResponseEntity<ServiceResponse<T>> getServiceResponse(boolean isSuccess, T data,
			HttpStatus httpStatus) {
		var serviceResponse = ServiceResponse.<T>builder().isSuccess(isSuccess).data(data).requestId(UUID.randomUUID())
				.requestCompletedAt(LocalDateTime.now()).build();

		var headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, null);

		return new ResponseEntity<>(serviceResponse, headers, httpStatus);
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
