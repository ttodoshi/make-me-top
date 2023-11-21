package org.example.feedback.exception.classes.feedback;

public class UnexpectedRatingValueException extends RuntimeException {
    public UnexpectedRatingValueException() {
        super("Непредусмотренное значение рейтинга");
    }
}

