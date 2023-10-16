package org.example.exception.classes.progressEX;

public class TeachingInProcessException extends RuntimeException {
    public TeachingInProcessException() {
        super("В данный момент происходит обучение другой группы");
    }
}
