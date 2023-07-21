package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MmtrAuthResponse {
    private Boolean isSuccess;
    private AuthResponseUser object;
}
