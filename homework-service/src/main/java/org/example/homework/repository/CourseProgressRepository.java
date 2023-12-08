package org.example.homework.repository;

import org.example.homework.dto.group.CurrentKeeperGroupDto;
import org.example.homework.dto.progress.CourseWithThemesProgressDto;

public interface CourseProgressRepository {
    CourseWithThemesProgressDto getCourseProgress(Long explorerId);

    CurrentKeeperGroupDto getCurrentGroup();
}
