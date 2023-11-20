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
public class KeeperListService {
    private final GalaxyRepository galaxyRepository;
    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public List<PersonWithGalaxyAndSystemsDto> getKeepers() {
        List<GetGalaxyInformationDto> galaxies = galaxyRepository.findAll();
        return galaxies.stream()
                .flatMap(g -> g.getKeepers()
                        .stream()
                        .map(k -> {
                                    PersonWithGalaxyAndSystemsDto keeper = mapper.map(
                                            k, PersonWithGalaxyAndSystemsDto.class);
                                    keeper.setGalaxyId(g.getGalaxyId());
                                    keeper.setGalaxyName(g.getGalaxyName());
                                    return keeper;
                                }
                        )
                ).collect(Collectors.toList());
    }
}
