package org.example.person.service.api.progress;

import org.example.person.dto.mark.CourseMarkDto;

import java.util.Optional;

public interface CourseMarkService {
    Optional<CourseMarkDto> findById(String authorizationHeader, Long explorerId);
}
