package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.dto.keeper.KeeperWithGalaxyDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperService {
    private final ModelMapper mapper;

    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;
    @Setter
    private String token;

    public List<KeeperWithGalaxyDTO> getKeepers(String sort, Integer galaxyId, Integer systemId) {
        KeeperList keeperList = getKeeperList();
        return keeperList.getKeepers();
    }

    private KeeperList getKeeperList() {
        GalaxyInformationGetResponse[] galaxies = sendGetGalaxiesRequest();
        List<KeeperWithGalaxyDTO> keepers = new LinkedList<>();
        for (GalaxyInformationGetResponse galaxy : galaxies) {
            List<KeeperWithGalaxyDTO> keepersFromGalaxy = galaxy.getKeepers().stream()
                    .map(k -> {
                        KeeperWithGalaxyDTO keeper = mapper.map(k, KeeperWithGalaxyDTO.class);
                        return keeper
                                .withGalaxyId(galaxy.getGalaxyId())
                                .withGalaxyName(galaxy.getGalaxyName());
                    })
                    .collect(Collectors.toList());
            keepers.addAll(keepersFromGalaxy);
        }
        return new AllKeeperList(keepers);
    }

    private GalaxyInformationGetResponse[] sendGetGalaxiesRequest() {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("galaxy/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GalaxyInformationGetResponse[].class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }
}
