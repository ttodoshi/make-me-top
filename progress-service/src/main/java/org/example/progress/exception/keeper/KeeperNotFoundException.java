package org.example.progress.exception.keeper;

public class KeeperNotFoundException extends RuntimeException {
    public KeeperNotFoundException() {
        super("Хранитель не найден");
    }
}
