package org.example.galaxy.exception.dependency;

public class DependencyCouldNotBeCreatedException extends RuntimeException {
    public DependencyCouldNotBeCreatedException(Long childId, Long parentId) {
        super("Зависимость между системами " + childId + " и " + parentId + " не может существовать");
    }
}
