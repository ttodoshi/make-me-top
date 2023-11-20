package org.example.homework.exception.classes.explorer;

public class ExplorerGroupIsNotOnCourseException extends RuntimeException {
    public ExplorerGroupIsNotOnCourseException(Integer groupId, Integer courseId) {
        super("Группа " + groupId + " не изучает курс " + courseId);
    }
}
