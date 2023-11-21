package org.example.progress.service;

import org.example.progress.dto.progress.CourseWithThemesProgressDto;
import org.example.grpc.ExplorersService;

public interface CourseThemesProgressService {
    CourseWithThemesProgressDto getThemesProgress(ExplorersService.Explorer explorer);
}
