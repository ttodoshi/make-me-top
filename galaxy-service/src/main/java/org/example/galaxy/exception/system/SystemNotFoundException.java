package org.example.galaxy.exception.system;

public class SystemNotFoundException extends RuntimeException {
    public SystemNotFoundException(Long systemId) {
        super("Не удалось найти информацию о системе " + systemId + " в памяти бортового компьютера");
    }
}
