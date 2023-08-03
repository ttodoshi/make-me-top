package org.example.exception.classes.keeperEX;

public class KeeperNotOnCourseException extends RuntimeException {
    public KeeperNotOnCourseException(Integer keeperId, Integer courseId) {
        super("Хранитель " + keeperId + " не отвечает за курс " + courseId);
    }
}
