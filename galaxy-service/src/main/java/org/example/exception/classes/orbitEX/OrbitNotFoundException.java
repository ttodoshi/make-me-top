package org.example.exception.classes.orbitEX;


public class OrbitNotFoundException extends RuntimeException {
    public OrbitNotFoundException() {
        super("Орбита не найдена");
    }
}
