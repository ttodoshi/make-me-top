package org.example.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class UserRequest {
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
