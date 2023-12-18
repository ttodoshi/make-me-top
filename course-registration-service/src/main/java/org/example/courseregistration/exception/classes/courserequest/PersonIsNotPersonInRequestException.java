package org.example.courseregistration.exception.classes.courserequest;

public class PersonIsNotPersonInRequestException extends RuntimeException {
    public PersonIsNotPersonInRequestException() {
        super("Вы не являетесь отправителем этого запроса");
    }
}

