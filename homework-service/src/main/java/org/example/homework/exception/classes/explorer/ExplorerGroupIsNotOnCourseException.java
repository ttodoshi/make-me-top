package org.example.homework.exception.classes.explorer;

public class ExplorerGroupIsNotOnCourseException extends RuntimeException {
    public ExplorerGroupIsNotOnCourseException(Long groupId, Long courseId) {
        super("Группа " + groupId + " не изучает курс " + courseId);
    }
}
