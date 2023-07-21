package org.example.exception.classes.requestEX;

public class PersonIsStudyingException extends RuntimeException {
    public PersonIsStudyingException() {
        super("В данный момент происходит изучение другого курса");
    }
}
