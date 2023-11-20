package org.example.progress.exception.classes.mark;

public class UnexpectedMarkValueException extends RuntimeException {
    public UnexpectedMarkValueException() {
        super("Непредусмотренное значение оценки");
    }
}
