package org.example.exception.classes.personEX;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
        super("Не удалось найти информацию о данном человеке в памяти бортового компьютера");
    }
}
