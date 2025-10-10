/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.utils;

public class Constants {

	Constants() {
	}

	public static class Auth {
		Auth() {

		}
		public static final String TOKEN = "token";
		public static final String AUTH_HEADER = "Authorization";
		public static final String BEARER = "Bearer ";
		public static final String NO_TOKEN_COOKIE_HEADER = "token=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None";
	}

	public static class Endpoints {
		Endpoints() {
		}
		public static final String BASE_URL = "/api";
	}
}
