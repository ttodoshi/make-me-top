package org.example.exception.classes.progressEX;

public class UnexpectedProgressValueException extends RuntimeException {
    public UnexpectedProgressValueException() {
        super("Непредусмотренное значение прогресса");
    }
}
