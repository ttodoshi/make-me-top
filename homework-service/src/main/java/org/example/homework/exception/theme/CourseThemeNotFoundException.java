package org.example.homework.exception.theme;

public class CourseThemeNotFoundException extends RuntimeException {
    public CourseThemeNotFoundException(Long courseThemeId) {
        super("Система не обнаружила у себя в памяти тему курса " + courseThemeId);
    }
}
