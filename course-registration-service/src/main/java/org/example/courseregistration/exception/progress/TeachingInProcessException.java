package org.example.courseregistration.exception.progress;

public class TeachingInProcessException extends RuntimeException {
    public TeachingInProcessException() {
        super("В данный момент происходит обучение другой группы");
    }
}
