/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.utils;

import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ResponseType;

public class CommonUtils {
	public static ResponseData generateResponseData(String alertType, ResponseSeverity severity) {
		ResponseType alert;

		try {
			alert = ResponseType.valueOf(alertType);
			return ResponseData.builder().responseType(alert).description(alert.getAlertMessage())
					.responseSeverity(severity).build();
		} catch (Exception e) {
			return ResponseData.builder().responseType(ResponseType.COMMON).description(alertType)
					.responseSeverity(severity).build();
		}

	}
}
