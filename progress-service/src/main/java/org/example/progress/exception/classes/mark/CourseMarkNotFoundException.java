package org.example.progress.exception.classes.mark;

public class CourseMarkNotFoundException extends RuntimeException {
    public CourseMarkNotFoundException(Long explorerId) {
        super("Оценка для исследователя " + explorerId + " не найдена");
    }
}
