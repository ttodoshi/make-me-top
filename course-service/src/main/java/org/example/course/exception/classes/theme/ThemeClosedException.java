package org.example.course.exception.classes.theme;

public class ThemeClosedException extends RuntimeException {
    public ThemeClosedException(Integer themeId) {
        super("Тема " + themeId + " ещё закрыта для вас");
    }
}
