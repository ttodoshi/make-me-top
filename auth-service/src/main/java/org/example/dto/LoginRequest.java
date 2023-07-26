package org.example.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private String role;

    @Override
    public String toString() {
        return "{" +
                "\"login\":\"" + login + "\"," +
                "\"password\":\"" + password + "\"}";
    }
}
