package org.example.feedback.exception.classes.progress;

public class CourseNotCompletedException extends RuntimeException {
    public CourseNotCompletedException(Long courseId) {
        super("Курс " + courseId + " ещё не завершён");
    }
}
