package org.example.exception.classes.coursethemeEX;

public class ThemeFromDifferentCourseException extends RuntimeException {
    public ThemeFromDifferentCourseException(Integer themeId, Integer groupId) {
        super(String.format("Тема %d не относится к курсу, который изучает группа %d", themeId, groupId));
    }
}
