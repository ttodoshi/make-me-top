package org.example.exception.classes.dependencyEX;

public class DependencyAlreadyExistsException extends RuntimeException {

    public DependencyAlreadyExistsException() {
        super("Зависимость уже существует");
    }
}
