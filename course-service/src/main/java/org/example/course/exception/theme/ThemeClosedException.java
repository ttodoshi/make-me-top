package org.example.course.exception.theme;

public class ThemeClosedException extends RuntimeException {
    public ThemeClosedException(Long themeId) {
        super("Тема " + themeId + " ещё закрыта для вас");
    }
}
