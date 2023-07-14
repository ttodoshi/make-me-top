package org.example.exception.classes.dependencyEX;

public class DependencyCouldNotBeCreatedException extends RuntimeException {
    public DependencyCouldNotBeCreatedException() {
        super("Такой зависимости не может существовать");
    }
}
