package com.josip.reactiveluxury.module.domain.user;

public enum UserRole {
    ADMIN,
    COMPETITOR,
    SYSTEM_USER;

    public boolean isSystemUser() {
        return this == UserRole.SYSTEM_USER;
    }

    public boolean isAdmin() {
        return this == UserRole.ADMIN;
    }
}
