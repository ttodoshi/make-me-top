package org.example.exception.classes.userEX;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Бортовой компьютер не смог вас идентифицировать");
    }
}
