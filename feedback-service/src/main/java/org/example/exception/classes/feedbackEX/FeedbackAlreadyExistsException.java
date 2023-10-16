package org.example.exception.classes.feedbackEX;

public class FeedbackAlreadyExistsException extends RuntimeException {
    public FeedbackAlreadyExistsException() {
        super("Оценка уже существует");
    }
}

