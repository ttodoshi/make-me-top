package org.example.feedback.exception.classes.person;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(Long personId) {
        super(String.format("Бортовой компьютер не смог найти человека с id %d", personId));
    }
}
