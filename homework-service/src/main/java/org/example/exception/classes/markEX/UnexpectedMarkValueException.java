package org.example.exception.classes.markEX;

public class UnexpectedMarkValueException extends RuntimeException {
    public UnexpectedMarkValueException() {
        super("Непредусмотренное значение оценки");
    }
}
