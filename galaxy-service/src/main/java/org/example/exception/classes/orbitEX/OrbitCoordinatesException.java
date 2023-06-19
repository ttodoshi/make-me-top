package org.example.exception.classes.orbitEX;

public class OrbitCoordinatesException extends RuntimeException {
    public OrbitCoordinatesException() {
        super("По заданым координатам в навигаторе уже хранится объект");
    }
}
