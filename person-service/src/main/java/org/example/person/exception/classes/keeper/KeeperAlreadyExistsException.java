package org.example.person.exception.classes.keeper;

public class KeeperAlreadyExistsException extends RuntimeException {
    public KeeperAlreadyExistsException(Long courseId) {
        super("Человек уже является хранителем на курсе " + courseId);
    }
}
