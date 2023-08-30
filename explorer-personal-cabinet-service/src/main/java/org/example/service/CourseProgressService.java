package org.example.service;

import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.dto.courseprogress.CoursesState;

import java.util.Map;

public interface CourseProgressService {
    CoursesState getCoursesProgressForCurrentUser(Integer galaxyId);

    CourseWithThemesProgress getThemesProgressByCourseId(Integer courseId);

    boolean hasUncompletedParents(Integer personId, Integer systemId);

    Map<String, String> leaveCourse(Integer courseId);
}
