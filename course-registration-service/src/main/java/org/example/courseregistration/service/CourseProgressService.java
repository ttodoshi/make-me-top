package org.example.courseregistration.service;

public interface CourseProgressService {
    boolean isCourseOpenedForAuthenticatedPerson(Integer courseId);

    boolean isAuthenticatedPersonCurrentlyStudying(Integer galaxyId);
}
