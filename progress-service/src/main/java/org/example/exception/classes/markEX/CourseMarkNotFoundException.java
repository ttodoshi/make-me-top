package org.example.exception.classes.markEX;

public class CourseMarkNotFoundException extends RuntimeException {
    public CourseMarkNotFoundException(Integer explorerId) {
        super("Оценка для исследователя " + explorerId + " не найдена");
    }
}
