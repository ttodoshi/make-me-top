package org.example.course.exception.course;

public class CourseNotFoundInGalaxyException extends RuntimeException {
    public CourseNotFoundInGalaxyException(Long courseId, Long galaxyId) {
        super("Курс " + courseId + " не найден в галактике " + galaxyId);
    }
}
