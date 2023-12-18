package org.example.person.repository;

import org.example.person.dto.mark.CourseMarkDto;

import java.util.Optional;

public interface CourseMarkRepository {
    Optional<CourseMarkDto> findById(Long explorerId);
}
