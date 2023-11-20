package org.example.homework.exception.classes.mark;

public class UnexpectedMarkValueException extends RuntimeException {
    public UnexpectedMarkValueException() {
        super("Непредусмотренное значение оценки");
    }
}
