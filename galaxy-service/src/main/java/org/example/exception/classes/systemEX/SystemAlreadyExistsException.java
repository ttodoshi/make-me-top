package org.example.exception.classes.systemEX;

public class SystemAlreadyExistsException extends RuntimeException {
    public SystemAlreadyExistsException(String name) {
        super("По информации бортового компьютера, система '" + name + "' уже существует в данной галактике");
    }
}
