package org.example.galaxy.utils.mapper;

import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.example.grpc.PeopleService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PersonWithSystemsMapper {
    public static List<PersonWithSystemsDto> mapToKeepersWithSystems(Map<Long, KeepersService.KeepersPeopleByCourseIdInResponse.KeeperList> keepersMap) {
        return mapToPersonWithSystems(
                keepersMap
                        .entrySet()
                        .stream()
                        .flatMap(s -> s.getValue()
                                .getPersonList()
                                .stream()
                                .map(k -> Map.entry(k, s.getKey()))
                        ).collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                        ))
        );
    }

    public static List<PersonWithSystemsDto> mapToExplorersWithSystems(Map<Long, ExplorersService.ExplorersPeopleByGroup_CourseIdInResponse.ExplorerList> explorerMap) {
        return mapToPersonWithSystems(
                explorerMap
                        .entrySet()
                        .stream()
                        .flatMap(s -> s.getValue()
                                .getPersonList()
                                .stream()
                                .map(e -> Map.entry(e, s.getKey()))
                        ).collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                        ))
        );
    }

    private static List<PersonWithSystemsDto> mapToPersonWithSystems(Map<PeopleService.PersonWithRating, List<Long>> personMap) {
        return personMap.entrySet()
                .stream()
                .map(e -> new PersonWithSystemsDto(e.getKey().getPersonId(), e.getKey().getFirstName(), e.getKey().getLastName(), e.getKey().getPatronymic(), e.getKey().getRating(), e.getValue()))
                .collect(Collectors.toList());
    }
}
