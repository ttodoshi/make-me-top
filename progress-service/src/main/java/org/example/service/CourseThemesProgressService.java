package org.example.service;

import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.dto.explorer.ExplorerDto;

public interface CourseThemesProgressService {
    CourseWithThemesProgressDto getThemesProgress(ExplorerDto explorer);
}
