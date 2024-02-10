package org.example.feedback.exception.feedback;

public class FeedbackAlreadyExistsException extends RuntimeException {
    public FeedbackAlreadyExistsException() {
        super("Оценка уже существует");
    }
}
