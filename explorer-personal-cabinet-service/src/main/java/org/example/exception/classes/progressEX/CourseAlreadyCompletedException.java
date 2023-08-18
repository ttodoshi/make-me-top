package org.example.exception.classes.progressEX;

public class CourseAlreadyCompletedException extends RuntimeException {
    public CourseAlreadyCompletedException(Integer courseId) {
        super("Курс " + courseId + " уже завершён");
    }
}
