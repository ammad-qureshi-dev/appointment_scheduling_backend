/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response;

import lombok.Getter;

@Getter
public enum ResponseType {
	NOTIFICATION_NOT_FOUND("Notification not found"), INVALID_BOOKING_VIEW(
			"Cannot view past bookings."), BOOKING_NOT_FOUND("Could not find booking."), TIME_SLOT_TAKEN(
					"Could not book for that time slot, please find another"), CUSTOMER_ALREADY_EXISTS(
							"Customer already exists."), CUSTOMER_NOT_FOUND(
									"Could not find customer."), BUSINESS_NAME_ALREADY_EXISTS(
											"This business name already exists, please choose another one."), ACCOUNT_EXISTS_WITHIN_BUSINESS(
													"This account already exists within the business."), COMPANY_NOT_FOUND(
															"No business was found with that ID.");

	private final String alertMessage;

	ResponseType(String alertMessage) {
		this.alertMessage = alertMessage;
	}

}
