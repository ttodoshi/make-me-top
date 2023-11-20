package org.example.course.exception.classes.course;

public class CourseNotFoundInGalaxyException extends RuntimeException {
    public CourseNotFoundInGalaxyException(Integer courseId, Integer galaxyId) {
        super("Курс " + courseId + " не найден в галактике " + galaxyId);
    }
}
