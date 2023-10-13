package org.example.exception.classes.feedbackEX;

public class UnexpectedRatingValueException extends RuntimeException {
    public UnexpectedRatingValueException() {
        super("Непредусмотренное значение рейтинга");
    }
}

