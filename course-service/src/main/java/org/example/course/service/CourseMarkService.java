package org.example.course.service;

import org.example.course.dto.mark.CourseMarkDto;

import java.util.Optional;

public interface CourseMarkService {
    Optional<CourseMarkDto> findById(String authorizationHeader, Long explorerId);
}
