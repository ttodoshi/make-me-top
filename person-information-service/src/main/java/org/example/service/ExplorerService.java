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

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerService {
    private final GalaxyRepository galaxyRepository;

    private final InformationService informationService;
    private final ModelMapper mapper;

    public List<Object> getExplorers(String sort, Period period, Integer systemId) {
        PersonList explorerList = getExplorerList();
        return explorerList.getPeople().stream()
                .map(p -> {
                    Optional<CurrentCourseProgressDTO> currentCourseProgressDTOOptional = informationService.getCurrentCourseProgress(p.getPersonId());
                    if (currentCourseProgressDTOOptional.isPresent()) {
                        CurrentCourseProgressDTO currentCourseProgressDTO = currentCourseProgressDTOOptional.get();
                        return new ExplorerWithCurrentSystem(p, currentCourseProgressDTO.getCourseId(), currentCourseProgressDTO.getCourseTitle());
                    }
                    return p;
                })
                .collect(Collectors.toList());
    }

    private PersonList getExplorerList() {
        List<GalaxyInformationGetResponse> galaxies = galaxyRepository.getGalaxies();
        List<PersonWithRatingAndGalaxyDTO> explorers = new ArrayList<>();
        for (GalaxyInformationGetResponse galaxy : galaxies) {
            List<PersonWithRatingAndGalaxyDTO> explorersFromGalaxy = galaxy.getExplorers().stream()
                    .map(k -> {
                        PersonWithRatingAndGalaxyDTO explorer = mapper.map(k, PersonWithRatingAndGalaxyDTO.class);
                        return explorer
                                .withGalaxyId(galaxy.getGalaxyId())
                                .withGalaxyName(galaxy.getGalaxyName());
                    })
                    .collect(Collectors.toList());
            explorers.addAll(explorersFromGalaxy);
        }
        return new AllPersonList(explorers);
    }
}
