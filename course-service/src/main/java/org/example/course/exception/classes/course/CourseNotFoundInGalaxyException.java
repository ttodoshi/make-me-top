package org.example.course.exception.classes.course;

public class CourseNotFoundInGalaxyException extends RuntimeException {
    public CourseNotFoundInGalaxyException(Long courseId, Long galaxyId) {
        super("Курс " + courseId + " не найден в галактике " + galaxyId);
    }
}
