package org.example.exception.classes.coursethemeEX;

public class ThemeClosedException extends RuntimeException {
    public ThemeClosedException(Integer themeId) {
        super("Тема " + themeId + " ещё закрыта для вас");
    }
}
