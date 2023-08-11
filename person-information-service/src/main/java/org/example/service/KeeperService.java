package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.person.PersonWithRatingAndGalaxyDTO;
import org.example.logic.sort.AllPersonList;
import org.example.logic.sort.PersonList;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperService {
    private final ModelMapper mapper;
    private final GalaxyRequestSender galaxyRequestSender;

    @Setter
    private String token;

    public List<PersonWithRatingAndGalaxyDTO> getKeepers(String sort, Integer galaxyId, Integer systemId) {
        PersonList personList = getKeeperList();
        return personList.getPeople();
    }

    private PersonList getKeeperList() {
        galaxyRequestSender.setToken(token);
        GalaxyInformationGetResponse[] galaxies = galaxyRequestSender.sendGetGalaxiesRequest();
        List<PersonWithRatingAndGalaxyDTO> keepers = new LinkedList<>();
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
