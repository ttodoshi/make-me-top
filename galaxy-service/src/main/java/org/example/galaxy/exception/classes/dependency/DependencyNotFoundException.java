package org.example.galaxy.exception.classes.dependency;

public class DependencyNotFoundException extends RuntimeException {
    public DependencyNotFoundException() {
        super("Не удалось найти информацию о данной зависимости в памяти бортового компьютера");
    }
}
