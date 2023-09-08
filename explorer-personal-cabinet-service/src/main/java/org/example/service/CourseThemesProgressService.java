package org.example.service;

import org.example.dto.courseprogress.CourseWithThemesProgressDto;
import org.example.model.Explorer;

public interface CourseThemesProgressService {
    CourseWithThemesProgressDto getThemesProgress(Explorer explorer);
}
