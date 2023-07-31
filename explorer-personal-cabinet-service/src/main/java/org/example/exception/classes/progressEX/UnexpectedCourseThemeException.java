package org.example.exception.classes.progressEX;

public class UnexpectedCourseThemeException extends RuntimeException {
    public UnexpectedCourseThemeException(Integer actualThemeId, Integer expectedThemeId) {
        super("Ожидалось задание по теме: " + expectedThemeId + ", получено задание по теме: " + actualThemeId);
    }
}
