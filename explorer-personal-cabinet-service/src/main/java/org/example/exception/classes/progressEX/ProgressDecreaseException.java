package org.example.exception.classes.progressEX;

public class ProgressDecreaseException extends RuntimeException {
    public ProgressDecreaseException() {
        super("Вы не можете уменьшить существующий прогресс исследования системы");
    }
}
