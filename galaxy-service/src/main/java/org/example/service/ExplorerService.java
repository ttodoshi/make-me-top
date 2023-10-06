package org.example.service;

import org.example.dto.course.GetCourseDto;
import org.example.dto.person.PersonWithSystemsDto;

import java.util.List;

public interface ExplorerService {
    List<PersonWithSystemsDto> getExplorersWithSystems(List<GetCourseDto> courses);
}
