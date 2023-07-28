package org.example.exception.classes.personEX;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(Integer personId) {
        super("Человек " + personId + " не найден");
    }

    public PersonNotFoundException() {
        super("Человек не найден");
    }
}
