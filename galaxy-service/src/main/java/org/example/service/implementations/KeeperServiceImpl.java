package org.example.service.implementations;

import org.example.dto.course.GetCourseDto;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.service.KeeperService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeeperServiceImpl implements KeeperService {
    @Override
    public List<PersonWithSystemsDto> getKeepersWithSystems(List<GetCourseDto> courses) {
        return courses.stream()
                .flatMap(course -> course.getKeepers().stream()
                        .map(k -> Map.entry(k, course.getCourse().getCourseId())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .map(k -> new PersonWithSystemsDto(k.getKey().getPersonId(), k.getKey().getFirstName(), k.getKey().getLastName(), k.getKey().getPatronymic(), k.getKey().getRating(), k.getValue()))
                .collect(Collectors.toList());
    }
}
