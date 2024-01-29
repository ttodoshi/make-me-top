package org.example.courseregistration.service;

public interface CourseProgressService {
    boolean isCourseOpenedForAuthenticatedPerson(Long courseId);

    boolean isAuthenticatedPersonCurrentlyStudying();
}
