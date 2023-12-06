package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.galaxy.GalaxyDto;
import org.example.person.dto.galaxy.GetGalaxyInformationDto;
import org.example.person.dto.person.PersonWithGalaxiesAndSystemsDto;
import org.example.person.repository.GalaxyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerListService {
    private final GalaxyRepository galaxyRepository;

    @Transactional(readOnly = true)
    public List<PersonWithGalaxiesAndSystemsDto> getExplorers() {
        List<GetGalaxyInformationDto> galaxies = galaxyRepository.findAll();
        return galaxies.stream()
                .flatMap(g -> g.getExplorers().stream().map(p -> Map.entry(p, g)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()))
                ).entrySet()
                .stream()
                .map(e -> new PersonWithGalaxiesAndSystemsDto(
                        e.getKey(),
                        e.getValue()
                                .stream()
                                .map(g -> new GalaxyDto(
                                        g.getGalaxyId(),
                                        g.getGalaxyName(),
                                        g.getGalaxyDescription()
                                )).collect(Collectors.toList()))
                ).collect(Collectors.toList());
    }
}
