package org.example.auth.dto.mmtr;

import lombok.Data;

@Data
public class MmtrAuthResponseEmployeeDto {
    private String firstName;
    private String lastName;
    private String patronymic;
    private Long employeeId;
    private String email;
    private String phoneNumber;
    private String skype;
    private String telegram;
    private Boolean isVisiblePrivateData;
    private MmtrTokenInfoDto userToken;
}
