package org.example.courseregistration.exception.courserequest;

public class PersonIsNotPersonInRequestException extends RuntimeException {
    public PersonIsNotPersonInRequestException() {
        super("Вы не являетесь отправителем этого запроса");
    }
}

