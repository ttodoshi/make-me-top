package org.example.exception.classes.systemEX;

public class SystemNotFoundException extends RuntimeException {

    public SystemNotFoundException() {
        super("Система не найдена");
    }
}
