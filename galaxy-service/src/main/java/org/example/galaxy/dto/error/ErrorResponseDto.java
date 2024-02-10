package org.example.galaxy.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponseDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    private String errorCode;
    private String errorMessage;

    public ErrorResponseDto(String statusCode, String errorMessage) {
        timestamp = LocalDateTime.now();
        errorCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
