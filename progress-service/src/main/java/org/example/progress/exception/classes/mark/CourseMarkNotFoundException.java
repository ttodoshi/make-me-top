package org.example.progress.exception.classes.mark;

public class CourseMarkNotFoundException extends RuntimeException {
    public CourseMarkNotFoundException(Integer explorerId) {
        super("Оценка для исследователя " + explorerId + " не найдена");
    }
}
