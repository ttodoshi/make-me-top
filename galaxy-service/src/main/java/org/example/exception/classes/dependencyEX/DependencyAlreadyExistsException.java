package org.example.exception.classes.dependencyEX;

public class DependencyAlreadyExistsException extends RuntimeException {

    public DependencyAlreadyExistsException() {
        super("Зависимость между данными системами уже присутствует в памяти бортового компьютера");
    }
}
