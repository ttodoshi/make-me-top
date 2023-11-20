package org.example.courseregistration.exception.classes.progress;

public class PersonIsStudyingException extends RuntimeException {
    public PersonIsStudyingException() {
        super("В данный момент происходит изучение другого курса");
    }
}

