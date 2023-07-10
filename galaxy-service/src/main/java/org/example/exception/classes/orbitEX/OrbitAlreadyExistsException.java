package org.example.exception.classes.orbitEX;

public class OrbitAlreadyExistsException extends RuntimeException {
    public OrbitAlreadyExistsException() {
        super("По информации бортового компьютера, данная орбита уже существует");
    }
}
