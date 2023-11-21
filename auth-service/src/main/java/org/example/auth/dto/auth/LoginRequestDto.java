package org.example.auth.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequestDto {
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private String role;
}
