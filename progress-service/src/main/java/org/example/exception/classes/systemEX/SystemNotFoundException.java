package org.example.exception.classes.systemEX;

public class SystemNotFoundException extends RuntimeException {
    public SystemNotFoundException(Integer systemId) {
        super("Не удалось найти информацию о системе " + systemId + " в памяти бортового компьютера");
    }
}
