package org.example.service;

import org.example.dto.course.CourseGetResponse;
import org.example.dto.explorer.ExplorerWithSystemsDTO;

import java.util.List;

public interface ExplorerService {
    List<ExplorerWithSystemsDTO> getExplorersWithSystems(List<CourseGetResponse> courses);
}
