package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.galaxy.GetGalaxyInformationDto;
import org.example.person.dto.person.PersonWithGalaxyAndSystemsDto;
import org.example.person.repository.GalaxyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerListService {
    private final GalaxyRepository galaxyRepository;
    private final CourseProgressService courseProgressService;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<PersonWithGalaxyAndSystemsDto> getExplorers() {
        List<GetGalaxyInformationDto> galaxies = galaxyRepository.findAll();
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
                ).collect(Collectors.toList());
    }
}
