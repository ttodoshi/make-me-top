package org.example.auth.dto.mmtr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MmtrAuthResponseDto {
    private Boolean isSuccess;
    private MmtrAuthResponseEmployeeDto object;
}
