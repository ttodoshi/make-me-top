package org.example.exception.classes.requestEX;

public class PersonIsNotPersonInRequestException extends RuntimeException {
    public PersonIsNotPersonInRequestException() {
        super("Вы не являетесь отправителем этого запроса");
    }
}

