package org.example.course.exception.classes.theme;

public class ThemeClosedException extends RuntimeException {
    public ThemeClosedException(Long themeId) {
        super("Тема " + themeId + " ещё закрыта для вас");
    }
}
