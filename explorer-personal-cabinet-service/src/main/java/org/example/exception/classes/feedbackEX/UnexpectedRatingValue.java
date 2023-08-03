package org.example.exception.classes.feedbackEX;

public class UnexpectedRatingValue extends RuntimeException {
    public UnexpectedRatingValue() {
        super("Непредусмотренное значение рейтинга");
    }
}
