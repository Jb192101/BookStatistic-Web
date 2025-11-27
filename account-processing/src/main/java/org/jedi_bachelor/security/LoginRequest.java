package org.jedi_bachelor.security;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
