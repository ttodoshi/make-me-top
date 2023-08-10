package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.person.PersonWithRatingAndGalaxyDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.logic.sort.AllPersonList;
import org.example.logic.sort.PersonList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerService {
    private final ModelMapper mapper;
    private final GalaxyRequestSender galaxyRequestSender;

    @Setter
    private String token;

    public List<PersonWithRatingAndGalaxyDTO> getExplorers(String sort, Period period, Integer systemId) {
        PersonList explorerList = getExplorerList();
        return explorerList.getPeople();
    }

    private PersonList getExplorerList() {
        galaxyRequestSender.setToken(token);
        GalaxyInformationGetResponse[] galaxies = galaxyRequestSender.sendGetGalaxiesRequest();
        List<PersonWithRatingAndGalaxyDTO> explorers = new LinkedList<>();
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
