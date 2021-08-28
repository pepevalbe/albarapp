package com.pepe.albarapp.api.security;

import java.util.stream.Stream;

public enum UserRole {

	BILLING_USER("BILLING_USER"),
	HENS_BATCH_USER("HENS_BATCH_USER"),
	ADMIN("ADMIN");

	UserRole(String role) {
	}

	public static boolean contains(String role) {

		for (UserRole c : UserRole.values()) {
			if (c.name().equals(role)) {
				return true;
			}
		}

		return false;
	}

	public static String[] roles() {
		return Stream.of(values()).map(UserRole::toString).toArray(String[]::new);
	}
}
