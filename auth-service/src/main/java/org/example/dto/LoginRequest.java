package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class LoginRequest {
    private String login;
    private String password;
    private String role;

    @Override
    public String toString() {
        return "{" +
                "\"login\":\"" + login + "\"," +
                "\"password\":\"" + password + "\"}";
    }
}
