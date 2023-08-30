package org.example.service;

import org.example.dto.course.CourseGetResponse;
import org.example.dto.keeper.KeeperWithSystemsDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeeperServiceImpl implements KeeperService {
    @Override
    public List<KeeperWithSystemsDTO> getKeepersWithSystems(List<CourseGetResponse> courses) {
        return courses.stream()
                .flatMap(course -> course.getKeepers().stream()
                        .map(k -> Map.entry(k, course.getCourse().getCourseId())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .map(k -> new KeeperWithSystemsDTO(k.getKey().getPersonId(), k.getKey().getFirstName(), k.getKey().getLastName(), k.getKey().getPatronymic(), k.getKey().getRating(), k.getValue()))
                .collect(Collectors.toList());
    }
}
