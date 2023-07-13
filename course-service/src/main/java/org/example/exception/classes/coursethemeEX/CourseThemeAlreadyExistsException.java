package org.example.exception.classes.coursethemeEX;

public class CourseThemeAlreadyExistsException extends RuntimeException {
    public CourseThemeAlreadyExistsException() {
        super("По данным бортового компьютера данная тема уже существует");
    }
}
