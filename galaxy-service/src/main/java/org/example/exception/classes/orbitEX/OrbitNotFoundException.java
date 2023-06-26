package org.example.exception.classes.orbitEX;


public class OrbitNotFoundException extends RuntimeException {
    public OrbitNotFoundException() {
        super("Не удалось найти информацию о данной орбите в памяти бортового компьютера");
    }
}
