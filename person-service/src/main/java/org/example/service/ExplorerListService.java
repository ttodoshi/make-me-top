package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.dto.person.PersonWithGalaxyAndSystemsAndCurrentSystemDto;
import org.example.dto.person.PersonWithGalaxyAndSystemsDto;
import org.example.dto.progress.CurrentCourseProgressDto;
import org.example.repository.GalaxyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerListService {
    private final GalaxyRepository galaxyRepository;
    private final CourseProgressService courseProgressService;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<PersonWithGalaxyAndSystemsDto> getExplorers() {
        List<GetGalaxyInformationDto> galaxies = galaxyRepository.getGalaxies();
        return galaxies.stream()
                .flatMap(g -> g.getExplorers()
                        .stream()
                        .map(e -> {
                                    PersonWithGalaxyAndSystemsDto explorer = mapper.map(
                                            e, PersonWithGalaxyAndSystemsDto.class);
                                    explorer.setGalaxyId(g.getGalaxyId());
                                    explorer.setGalaxyName(g.getGalaxyName());
                                    return explorer;
                                }
                        )
                ).map(p -> {
                    Optional<CurrentCourseProgressDto> currentCourseProgress = courseProgressService
                            .getCurrentCourseProgress(p.getPersonId());
                    if (currentCourseProgress.isPresent()) {
                        CurrentCourseProgressDto progress = currentCourseProgress.get();
                        return new PersonWithGalaxyAndSystemsAndCurrentSystemDto(p, progress.getCourseId(), progress.getCourseTitle());
                    }
                    return p;
                })
                .collect(Collectors.toList());
    }
}
