package org.example.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UserAuthResponse {
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer employeeId;
}
