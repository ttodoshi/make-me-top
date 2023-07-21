package org.example.exception.classes.dependencyEX;

public class DependencyAlreadyExistsException extends RuntimeException {

    public DependencyAlreadyExistsException(Integer childId, Integer parentId) {
        super("Зависимость между системами " + childId + " и " + parentId + " уже присутствует в памяти бортового компьютера");
    }
}
