package org.example.exception.classes.explorerEX;

public class ExplorerGroupIsNotOnCourseException extends RuntimeException {
    public ExplorerGroupIsNotOnCourseException(Integer groupId, Integer courseId) {
        super("Группа " + groupId + " не изучает курс " + courseId);
    }
}
