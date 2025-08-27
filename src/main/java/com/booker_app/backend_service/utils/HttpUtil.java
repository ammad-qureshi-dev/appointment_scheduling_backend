/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

public class HttpUtil {

	public HttpUtil() {
		// sonar
	}

	public static void addCookie(HttpServletResponse response, String key, String value) {
		Cookie cookie = new Cookie(key.toUpperCase(), value);
		cookie.setPath("/");
		cookie.setHttpOnly(false);
		cookie.setSecure(true);
		cookie.setMaxAge(60 * 60);
		cookie.setAttribute("SameSite", "None");
		response.addCookie(cookie);
	}

	public static String retrieveValueFromToken(Cookie[] cookies, @NonNull String key) {
		for (var cookie : cookies) {
			if (key.equalsIgnoreCase(cookie.getName())) {
				return cookie.getValue();
			}
		}

		throw new RuntimeException("Cookie (key=" + key + ") not found");
	}
}
