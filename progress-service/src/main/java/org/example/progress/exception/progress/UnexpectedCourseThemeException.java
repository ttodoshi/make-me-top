package org.example.progress.exception.progress;

public class UnexpectedCourseThemeException extends RuntimeException {
    public UnexpectedCourseThemeException(Long actualThemeId, Long expectedThemeId) {
        super("Ожидалась тема: " + expectedThemeId + ", запрошенная тема: " + actualThemeId);
    }
}
