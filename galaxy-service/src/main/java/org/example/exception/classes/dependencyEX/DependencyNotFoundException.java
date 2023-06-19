package org.example.exception.classes.dependencyEX;

public class DependencyNotFoundException extends RuntimeException {
    public DependencyNotFoundException() {
        super("Зависимость не найдена");
    }
}
