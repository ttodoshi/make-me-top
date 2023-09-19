package org.example.exception.classes.personEX;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
        super("Бортовой компьютер не смог вас идентифицировать");
    }

    public PersonNotFoundException(Integer personId) {
        super(String.format("Бортовой компьютер не смог найти человека с id %d", personId));
    }
}
