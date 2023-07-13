package org.example.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    private String errorCode;
    private String errorMessage;

    public ErrorResponse(String statusCode, String errorMessage) {
        timestamp = new Date();
        errorCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
