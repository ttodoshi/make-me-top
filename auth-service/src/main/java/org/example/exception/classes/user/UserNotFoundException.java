package org.example.exception.classes.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Бортовой компьютер не смог вас идентифицировать");
    }
}
