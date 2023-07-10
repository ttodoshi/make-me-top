package org.example.exception.classes.systemEX;

public class SystemAlreadyExistsException extends RuntimeException {
    public SystemAlreadyExistsException() {
        super("По информации бортового компьютера, данная система уже существует");
    }
}
