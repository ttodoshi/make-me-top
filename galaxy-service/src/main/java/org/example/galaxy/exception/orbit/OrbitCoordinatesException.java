package org.example.galaxy.exception.orbit;

public class OrbitCoordinatesException extends RuntimeException {
    public OrbitCoordinatesException() {
        super("По заданным координатам в навигаторе уже хранится объект");
    }
}
