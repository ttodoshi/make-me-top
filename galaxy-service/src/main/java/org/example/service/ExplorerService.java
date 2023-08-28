package org.example.service;

import org.example.dto.course.CourseGetResponse;
import org.example.dto.explorer.ExplorerWithSystemsDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExplorerService {
    public List<ExplorerWithSystemsDTO> getExplorersWithSystems(List<CourseGetResponse> courses) {
        return courses.stream()
                .flatMap(course -> course.getExplorers().stream()
                        .map(explorer -> Map.entry(explorer, course.getCourse().getCourseId())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .map(e -> new ExplorerWithSystemsDTO(e.getKey().getPersonId(), e.getKey().getFirstName(), e.getKey().getLastName(), e.getKey().getPatronymic(), e.getKey().getRating(), e.getValue()))
                .collect(Collectors.toList());
    }
}
