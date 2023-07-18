package org.example.exception.classes.coursethemeEX;

public class CourseThemeAlreadyExistsException extends RuntimeException {
    public CourseThemeAlreadyExistsException(String title) {
        super("По данным бортового компьютера тема '" + title + "' уже существует");
    }
}
