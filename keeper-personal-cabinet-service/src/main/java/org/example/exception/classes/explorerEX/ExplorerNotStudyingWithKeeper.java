package org.example.exception.classes.explorerEX;

public class ExplorerNotStudyingWithKeeper extends RuntimeException {
    public ExplorerNotStudyingWithKeeper(Integer explorerId, Integer keeperId) {
        super("Исследователь " + explorerId + " не учится у хранителя " + keeperId);
    }
}
