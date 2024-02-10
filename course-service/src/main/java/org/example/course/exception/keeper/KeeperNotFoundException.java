package org.example.course.exception.keeper;

public class KeeperNotFoundException extends RuntimeException {
    public KeeperNotFoundException() {
        super("Хранитель не найден");
    }
}
