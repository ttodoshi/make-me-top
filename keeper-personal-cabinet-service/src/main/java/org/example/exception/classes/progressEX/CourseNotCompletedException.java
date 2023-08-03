package org.example.exception.classes.progressEX;

public class CourseNotCompletedException extends RuntimeException {
    public CourseNotCompletedException(Integer courseId) {
        super("Курс " + courseId + " ещё не завершён");
    }
}
