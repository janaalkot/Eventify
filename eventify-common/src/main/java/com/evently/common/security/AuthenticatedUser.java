package com.evently.common.security;

public record AuthenticatedUser(Long id, String email, String role, String fullName) {

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
