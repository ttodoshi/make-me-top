package org.example.exception.classes.dependencyEX;

public class DependencyCouldNotBeCreatedException extends RuntimeException {
    public DependencyCouldNotBeCreatedException(Integer childId, Integer parentId) {
        super("Зависимость между системами " + childId + " и " + parentId + " не может существовать");
    }
}
