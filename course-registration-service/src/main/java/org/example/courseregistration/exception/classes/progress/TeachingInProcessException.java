package org.example.courseregistration.exception.classes.progress;

public class TeachingInProcessException extends RuntimeException {
    public TeachingInProcessException() {
        super("В данный момент происходит обучение другой группы");
    }
}
