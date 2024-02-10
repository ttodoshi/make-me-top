package org.example.progress.service;

import org.example.grpc.ExplorersService;
import org.example.progress.dto.mark.ThemeMarkDto;
import org.example.progress.dto.progress.CourseWithThemesProgressDto;

import java.util.List;
import java.util.Map;

public interface CourseThemesProgressService {
    CourseWithThemesProgressDto getThemesProgress(String authorizationHeader, ExplorersService.Explorer explorer);

    Map<Long, List<ThemeMarkDto>> getExplorersThemesMarks(List<Long> explorerIds);
}
