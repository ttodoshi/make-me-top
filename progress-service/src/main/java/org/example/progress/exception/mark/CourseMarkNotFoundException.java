package org.example.progress.exception.mark;

public class CourseMarkNotFoundException extends RuntimeException {
    public CourseMarkNotFoundException(Long explorerId) {
        super("Оценка для исследователя " + explorerId + " не найдена");
    }
}
