package org.example.exception.classes.orbitEX;

public class OrbitAlreadyExistsException extends RuntimeException {
    public OrbitAlreadyExistsException() {
        super("Орбита уже существует");
    }
}
