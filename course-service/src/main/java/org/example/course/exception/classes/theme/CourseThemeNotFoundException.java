package org.example.course.exception.classes.theme;

public class CourseThemeNotFoundException extends RuntimeException {
    public CourseThemeNotFoundException(Integer courseThemeId) {
        super("Система не обнаружила у себя в памяти тему курса " + courseThemeId);
    }
}
