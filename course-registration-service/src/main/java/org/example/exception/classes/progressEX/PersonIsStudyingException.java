package org.example.exception.classes.progressEX;

public class PersonIsStudyingException extends RuntimeException {
    public PersonIsStudyingException() {
        super("В данный момент происходит изучение другого курса");
    }
}

