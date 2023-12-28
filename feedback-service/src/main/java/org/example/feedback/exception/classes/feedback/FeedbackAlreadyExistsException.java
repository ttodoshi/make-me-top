package org.example.feedback.exception.classes.feedback;

public class FeedbackAlreadyExistsException extends RuntimeException {
    public FeedbackAlreadyExistsException() {
        super("Оценка уже существует");
    }
}
