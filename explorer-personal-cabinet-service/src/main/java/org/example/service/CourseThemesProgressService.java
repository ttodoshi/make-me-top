package org.example.service;

import org.example.dto.courseprogress.CourseWithThemesProgress;
import org.example.model.Explorer;

public interface CourseThemesProgressService {
    CourseWithThemesProgress getThemesProgress(Explorer explorer);
}
