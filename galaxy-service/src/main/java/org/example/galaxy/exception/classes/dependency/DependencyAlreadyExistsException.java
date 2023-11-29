package org.example.galaxy.exception.classes.dependency;

public class DependencyAlreadyExistsException extends RuntimeException {
    public DependencyAlreadyExistsException(Long childId, Long parentId) {
        super("Зависимость между системами " + childId + " и " + parentId + " уже присутствует в памяти бортового компьютера");
    }
}
