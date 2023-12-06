package org.example.person.exception.classes.request;

public class PersonHaveOpenedCourseRegistrationRequestException extends RuntimeException {
    public PersonHaveOpenedCourseRegistrationRequestException(Long courseId) {
        super("У человека существует активный запрос на прохождение курса " + courseId);
    }
}
