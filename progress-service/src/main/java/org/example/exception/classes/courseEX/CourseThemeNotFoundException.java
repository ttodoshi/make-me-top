package org.example.exception.classes.courseEX;

public class CourseThemeNotFoundException extends RuntimeException {
    public CourseThemeNotFoundException(Integer courseThemeId) {
        super("Система не обнаружила у себя в памяти тему курса " + courseThemeId);
    }
}
