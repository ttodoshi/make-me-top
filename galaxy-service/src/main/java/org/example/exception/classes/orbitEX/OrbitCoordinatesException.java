package org.example.exception.classes.orbitEX;

public class OrbitCoordinatesException extends RuntimeException {
    public OrbitCoordinatesException() {
        super("По заданным координатам в навигаторе уже хранится объект");
    }
}
