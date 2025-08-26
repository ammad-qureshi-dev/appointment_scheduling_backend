/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.booker_app.backend_service.exceptions.ServiceResponseException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HashingUtil {

	public static String generateHashedInput(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(s.getBytes());
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new ServiceResponseException("Hash failed");
		}
	}
}