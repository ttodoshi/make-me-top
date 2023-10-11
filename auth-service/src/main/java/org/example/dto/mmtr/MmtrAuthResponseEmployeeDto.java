package org.example.dto.mmtr;

import lombok.Data;

@Data
public class MmtrAuthResponseEmployeeDto {
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer employeeId;
    private MmtrTokenInfoDto userToken;
}
