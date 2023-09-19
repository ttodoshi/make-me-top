package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.dto.person.PersonWithGalaxyAndSystemsDto;
import org.example.repository.GalaxyRepository;
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
        List<GetGalaxyInformationDto> galaxies = galaxyRepository.getGalaxies();
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
