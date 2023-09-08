package org.example.service;

import org.example.dto.course.GetCourseDto;
import org.example.dto.person.PersonWithSystemsDto;

import java.util.List;

public interface KeeperService {
    List<PersonWithSystemsDto> getKeepersWithSystems(List<GetCourseDto> courses);
}
