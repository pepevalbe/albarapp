package com.pepe.albarapp.service.dto;

public enum UserRole {

    USER("USER"),
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
}
