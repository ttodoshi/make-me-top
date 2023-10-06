package org.example.exception.classes.keeperEX;

public class KeeperNotForGroupException extends RuntimeException {
    public KeeperNotForGroupException() {
        super("Вы не являетесь хранителем данной группы");
    }
}
