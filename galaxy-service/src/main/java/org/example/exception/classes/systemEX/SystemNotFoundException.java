package org.example.exception.classes.systemEX;

public class SystemNotFoundException extends RuntimeException {

    public SystemNotFoundException() {
        super("Не удалось найти информацию о данной системе в памяти бортового компьютера");
    }
}
