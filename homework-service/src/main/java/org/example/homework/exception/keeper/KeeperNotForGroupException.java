package org.example.homework.exception.keeper;

public class KeeperNotForGroupException extends RuntimeException {
    public KeeperNotForGroupException() {
        super("Вы не являетесь хранителем данной группы");
    }
}
