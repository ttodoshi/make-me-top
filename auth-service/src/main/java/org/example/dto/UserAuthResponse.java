package org.example.dto;

import lombok.Data;

@Data
public class UserAuthResponse {
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer employeeId;
}
