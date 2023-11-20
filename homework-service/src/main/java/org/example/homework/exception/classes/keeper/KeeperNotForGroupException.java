package org.example.homework.exception.classes.keeper;

public class KeeperNotForGroupException extends RuntimeException {
    public KeeperNotForGroupException() {
        super("Вы не являетесь хранителем данной группы");
    }
}
