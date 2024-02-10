package org.example.homework.service;

import org.example.homework.dto.group.CurrentKeeperGroupDto;
import org.example.homework.dto.progress.CourseWithThemesProgressDto;

import java.util.Optional;

public interface CourseProgressService {
    CourseWithThemesProgressDto getCourseProgress(String authorizationHeader, Long explorerId);

    Optional<CurrentKeeperGroupDto> getCurrentGroup(String authorizationHeader);
}
