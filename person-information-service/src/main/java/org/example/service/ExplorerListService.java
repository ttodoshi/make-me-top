package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CurrentCourseProgressDto;
import org.example.dto.explorer.ExplorerWithCurrentSystemDto;
import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.dto.person.PersonWithGalaxyAndSystemsDto;
import org.example.service.sort.AllPersonList;
import org.example.service.sort.PersonList;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExplorerListService {
    private final GalaxyService galaxyService;
    private final CourseProgressService courseProgressService;

    private final ModelMapper mapper;

    public List<PersonWithGalaxyAndSystemsDto> getExplorers(String sort, Period period, Integer systemId) {
        PersonList explorerList = getExplorerList();
        return explorerList.getPeople()
                .stream()
                .map(p -> {
                    Optional<CurrentCourseProgressDto> currentCourseProgress = courseProgressService
                            .getCurrentCourseProgress(p.getPersonId());
                    if (currentCourseProgress.isPresent()) {
                        CurrentCourseProgressDto progress = currentCourseProgress.get();
                        return new ExplorerWithCurrentSystemDto(p, progress.getCourseId(), progress.getCourseTitle());
                    }
                    return p;
                })
                .collect(Collectors.toList());
    }

    private PersonList getExplorerList() {
        List<GetGalaxyInformationDto> galaxies = galaxyService.getGalaxies();
        Stream<PersonWithGalaxyAndSystemsDto> explorersStream = galaxies.stream()
                .flatMap(g -> g.getExplorers()
                        .stream()
                        .map(e -> {
                                    PersonWithGalaxyAndSystemsDto explorer = mapper.map(
                                            e, PersonWithGalaxyAndSystemsDto.class);
                                    return explorer
                                            .withGalaxyId(g.getGalaxyId())
                                            .withGalaxyName(g.getGalaxyName());
                                }
                        )
                );
        return new AllPersonList(explorersStream.collect(Collectors.toList()));
    }
}
