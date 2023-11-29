package org.example.homework.exception.classes.theme;

public class ThemeClosedException extends RuntimeException {
    public ThemeClosedException(Long themeId) {
        super("Тема " + themeId + " ещё закрыта для вас");
    }
}
