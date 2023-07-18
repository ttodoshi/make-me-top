package org.example.exception.classes.keeperEX;

public class KeeperNotFoundException extends RuntimeException {
    public KeeperNotFoundException(Integer keeperId) {
        super("Система не смогла найти у себя в памяти данные об хранителе " + keeperId);
    }
}
