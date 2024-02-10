package org.example.progress.service;

import org.example.progress.dto.course.CourseDto;

import java.util.Optional;

public interface CourseService {
    CourseDto findById(String authorizationHeader, Long courseId);
}
