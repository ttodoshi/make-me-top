package org.example.exception.responses;

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
    private HttpStatus code;
    private String message;


    public ErrorResponse(HttpStatus statusCode, String errorMessage) {
        timestamp = new Date();
        code = statusCode;
        message = errorMessage;
    }
}
