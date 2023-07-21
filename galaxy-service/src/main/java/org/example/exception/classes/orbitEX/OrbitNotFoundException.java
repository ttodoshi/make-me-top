package org.example.exception.classes.orbitEX;

public class OrbitNotFoundException extends RuntimeException {
    public OrbitNotFoundException(Integer orbitId) {
        super("Не удалось найти информацию о орбите " + orbitId + " в памяти бортового компьютера");
    }
}
