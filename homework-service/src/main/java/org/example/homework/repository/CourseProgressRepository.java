package org.example.homework.repository;

import org.example.homework.dto.group.CurrentKeeperGroupDto;
import org.example.homework.dto.progress.CourseWithThemesProgressDto;

import java.util.Optional;

public interface CourseProgressRepository {
    CourseWithThemesProgressDto getCourseProgress(Long explorerId);

    Optional<CurrentKeeperGroupDto> getCurrentGroup();
}
