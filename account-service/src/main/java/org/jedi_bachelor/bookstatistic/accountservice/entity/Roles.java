package org.jedi_bachelor.bookstatistic.accountservice.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Roles {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MODERATOR("ROLE_MODERATOR");

    private final String name;

    public String toString() {
        return this.name;
    }
}
