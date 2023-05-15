package org.example.model;

import lombok.Data;

@Data
public class UserRequest {
    private String login;
    private String password;


    @Override
    public String toString() {
        return "{" +
                "\"login\":\"" + login + "\"," +
                "\"password\":\"" + password + "\"}";
    }
}
