package org.example.course.exception.theme;

public class CourseThemeAlreadyExistsException extends RuntimeException {
    public CourseThemeAlreadyExistsException(String title) {
        super("По данным бортового компьютера тема '" + title + "' уже существует");
    }
}
