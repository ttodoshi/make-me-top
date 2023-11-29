package org.example.galaxy.exception.classes.orbit;

public class OrbitNotFoundException extends RuntimeException {
    public OrbitNotFoundException(Long orbitId) {
        super("Не удалось найти информацию о орбите " + orbitId + " в памяти бортового компьютера");
    }
}
