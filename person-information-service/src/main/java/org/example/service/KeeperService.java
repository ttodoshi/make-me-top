package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.person.PersonWithRatingAndGalaxyDTO;
import org.example.repository.custom.GalaxyRepository;
import org.example.service.sort.AllPersonList;
import org.example.service.sort.PersonList;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperService {
    private final GalaxyRepository galaxyRepository;

    private final ModelMapper mapper;

    public List<PersonWithRatingAndGalaxyDTO> getKeepers(String sort, Integer galaxyId, Integer systemId) {
        PersonList personList = getKeeperList();
        return personList.getPeople();
    }

    private PersonList getKeeperList() {
        List<GalaxyInformationGetResponse> galaxies = galaxyRepository.getGalaxies();
        List<PersonWithRatingAndGalaxyDTO> keepers = new ArrayList<>();
        for (GalaxyInformationGetResponse galaxy : galaxies) {
            List<PersonWithRatingAndGalaxyDTO> keepersFromGalaxy = galaxy.getKeepers().stream()
                    .map(k -> {
                        PersonWithRatingAndGalaxyDTO keeper = mapper.map(k, PersonWithRatingAndGalaxyDTO.class);
                        return keeper
                                .withGalaxyId(galaxy.getGalaxyId())
                                .withGalaxyName(galaxy.getGalaxyName());
                    })
                    .collect(Collectors.toList());
            keepers.addAll(keepersFromGalaxy);
        }
        return new AllPersonList(keepers);
    }
}
