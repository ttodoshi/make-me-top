package org.example.galaxy.exception.classes.system;

public class SystemsFromDifferentGalaxiesException extends RuntimeException {
    public SystemsFromDifferentGalaxiesException(Long childId, Long parentId) {
        super("Системы " + childId + " и " + parentId + " находятся в разных галактиках");
    }
}
