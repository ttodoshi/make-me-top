package org.example.exception.classes.orbitEX;

public class OrbitDeleteException extends RuntimeException {
    public OrbitDeleteException() {
        super("Ошибка удаления орбиты");
    }
}
