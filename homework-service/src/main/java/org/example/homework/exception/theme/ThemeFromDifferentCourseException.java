package org.example.homework.exception.theme;

public class ThemeFromDifferentCourseException extends RuntimeException {
    public ThemeFromDifferentCourseException(Long themeId, Long groupId) {
        super(String.format("Тема %d не относится к курсу, который изучает группа %d", themeId, groupId));
    }
}
