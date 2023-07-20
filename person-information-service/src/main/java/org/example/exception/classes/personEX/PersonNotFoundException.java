package org.example.exception.classes.personEX;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException() {
        super("Человек не найден");
    }
}
