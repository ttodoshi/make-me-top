package org.example.picture.exception.person;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
        super("Бортовой компьютер не смог вас идентифицировать");
    }
}
