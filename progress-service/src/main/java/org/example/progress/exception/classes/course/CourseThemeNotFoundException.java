package org.example.progress.exception.classes.course;

public class CourseThemeNotFoundException extends RuntimeException {
    public CourseThemeNotFoundException(Integer courseThemeId) {
        super("Система не обнаружила у себя в памяти тему курса " + courseThemeId);
    }
}
