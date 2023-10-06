package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponseDto {
    private Boolean isSuccess;
    private AuthResponseEmployeeDto object;
}
