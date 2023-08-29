package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.courseprogress.CurrentCourseProgressDTO;
import org.example.dto.explorer.ExplorerWithCurrentSystem;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.person.PersonWithRatingAndGalaxyDTO;
import org.example.repository.custom.GalaxyRepository;
import org.example.service.sort.AllPersonList;
import org.example.service.sort.PersonList;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExplorerListService {
    private final GalaxyRepository galaxyRepository;

    private final CourseProgressService courseProgressService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<PersonWithRatingAndGalaxyDTO> getExplorers(String sort, Period period, Integer systemId) {
        PersonList explorerList = getExplorerList();
        return explorerList.getPeople()
                .stream()
                .map(p -> {
                    Optional<CurrentCourseProgressDTO> currentCourseProgress = courseProgressService
                            .getCurrentCourseProgress(p.getPersonId());
                    if (currentCourseProgress.isPresent()) {
                        CurrentCourseProgressDTO progress = currentCourseProgress.get();
                        return new ExplorerWithCurrentSystem(p, progress.getCourseId(), progress.getCourseTitle());
                    }
                    return p;
                })
                .collect(Collectors.toList());
    }

    private PersonList getExplorerList() {
        List<GalaxyInformationGetResponse> galaxies = galaxyRepository.getGalaxies();
        Stream<PersonWithRatingAndGalaxyDTO> explorersStream = galaxies.stream()
                .flatMap(g -> g.getExplorers()
                        .stream()
                        .map(e -> {
                                    PersonWithRatingAndGalaxyDTO explorer = mapper.map(
                                            e, PersonWithRatingAndGalaxyDTO.class);
                                    return explorer
                                            .withGalaxyId(g.getGalaxyId())
                                            .withGalaxyName(g.getGalaxyName());
                                }
                        )
                );
        return new AllPersonList(explorersStream.collect(Collectors.toList()));
    }
}
