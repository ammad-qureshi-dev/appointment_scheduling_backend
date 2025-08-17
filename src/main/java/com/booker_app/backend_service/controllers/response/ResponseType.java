/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response;

import lombok.Getter;

@Getter
public enum ResponseType {
	EMAIL_ALREADY_TAKEN("Email already taken"), COMMON(""), USER_ALREADY_EXISTS(
			"User already exists"), SERVICE_ALREADY_EXISTS("Service already exists"), CANNOT_REMOVE_OWNER(
					"User has role-type of OWNER, cannot remove"), USER_NOT_FOUND(
							"User not found"), UNREGISTERED_USER_EMAIL(
									"User's email not registered"), EMPLOYEE_NOT_FOUND(
											"Employee not found"), EMPLOYEE_EMAIL_ALREADY_EXISTS(
													"An employee already exists with this email"), INVALID_APPOINTMENT_REQUEST_TIME(
															"End Time must occur after Start Time"), APPOINTMENT_DATE_IN_PAST(
																	"Appointment Date cannot be in the past"), APPOINTMENT_NOT_FOUND(
																			"Appointment not found"), TIME_SLOT_TAKEN(
																					"Could not book for that time slot, please find another"), CUSTOMER_ALREADY_EXISTS(
																							"Customer already exists."), CUSTOMER_NOT_FOUND(
																									"Could not find customer."), COMPANY_ALREADY_EXISTS(
																											"This business name already exists, please choose another one."), COMPANY_NOT_FOUND(
																													"No business was found with that ID.");

	private final String alertMessage;

	ResponseType(String alertMessage) {
		this.alertMessage = alertMessage;
	}

}
