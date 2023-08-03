package org.example.exception.classes.feedbackEX;

public class FeedbackAlreadyExists extends RuntimeException {
    public FeedbackAlreadyExists() {
        super("Оценка уже существует");
    }
}
