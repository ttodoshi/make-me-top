package org.example.service;

import org.example.dto.progress.CourseWithThemesProgressDto;
import org.example.grpc.ExplorersService;

public interface CourseThemesProgressService {
    CourseWithThemesProgressDto getThemesProgress(ExplorersService.Explorer explorer);
}
