package org.example.exception.classes.systemEX;

public class SystemAlreadyExistsException extends RuntimeException {
    public SystemAlreadyExistsException() {
        super("Система уже существует");
    }
}
