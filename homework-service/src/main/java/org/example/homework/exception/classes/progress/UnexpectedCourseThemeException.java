package org.example.homework.exception.classes.progress;

public class UnexpectedCourseThemeException extends RuntimeException {
    public UnexpectedCourseThemeException(Integer expectedThemeId, Integer actualThemeId) {
        super("Ожидалось задание по теме: " + expectedThemeId + ", получено задание по теме: " + actualThemeId);
    }
}
