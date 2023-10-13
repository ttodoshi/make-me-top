package org.example.exception.classes.explorerEX;

public class ExplorerNotStudyingWithKeeperException extends RuntimeException {
    public ExplorerNotStudyingWithKeeperException(Integer explorerId, Integer keeperId) {
        super("Исследователь " + explorerId + " не учится у хранителя " + keeperId);
    }
}
