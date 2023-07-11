package org.example.exception.classes.keeperEX;

public class KeeperNotFoundException extends RuntimeException {
    public KeeperNotFoundException() {
        super("Система не смогла найти у себя в памяти данные об этом хранителе");
    }
}
