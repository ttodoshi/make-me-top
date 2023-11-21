package org.example.progress.exception.classes.progress;

public class UnexpectedCourseThemeException extends RuntimeException {
    public UnexpectedCourseThemeException(Integer actualThemeId, Integer expectedThemeId) {
        super("Ожидалась тема: " + expectedThemeId + ", запрошенная тема: " + actualThemeId);
    }
}
