package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.PlanetDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.Planet;
import org.example.model.StarSystem;
import org.example.repository.PlanetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@PropertySource(value = {"classpath:config.properties"})
public class PlanetService {
    private final PlanetRepository planetRepository;

    private final ModelMapper mapper;

    private final RestTemplate restTemplate;

    @Setter
    private String token;

    private final Logger logger = Logger.getLogger(PlanetService.class.getName());

    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;
    @Value("${get_system_by_id}")
    private String GET_SYSTEM_BY_ID_URL;

    public List<Planet> getPlanetsListBySystemId(Integer systemId) {
        if (checkSystemExistence(systemId))
            return planetRepository.getListPlanetBySystemId(systemId);
        throw new SystemNotFoundException();
    }

    @Transactional
    public List<Planet> addPlanet(List<PlanetDTO> planets, Integer galaxyId) {
        List<Planet> savedPlanets = new LinkedList<>();
        List<Planet> planetsByGalaxyId;
        try {
            planetsByGalaxyId = planetRepository.getAllPlanetsByGalaxyId(galaxyId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
        for (PlanetDTO planet : planets) {
            if (planetsByGalaxyId.stream().noneMatch(
                    x -> Objects.equals(x.getPlanetName(), planet.getPlanetName()))) {
                if (checkSystemExistence(planet.getSystemId()))
                    savedPlanets.add(planetRepository.save(mapper.map(planet, Planet.class)));
                else
                    throw new SystemNotFoundException();
            } else
                throw new PlanetAlreadyExistsException();
        }
        return savedPlanets;
    }

    public Map<String, String> deletePlanetById(Integer planetId) {
        try {
            planetRepository.deleteById(planetId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Планета подлежит уничтожению для создания межгалактической трассы");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new PlanetNotFoundException();
        }
    }

    public Planet updatePlanet(Integer planetId,
                               Integer galaxyId,
                               PlanetDTO planet) {
        if (!checkSystemExistence(planet.getSystemId()))
            throw new SystemNotFoundException();
        Optional<Planet> updatedPlanetOptional = planetRepository.findById(planetId);
        Planet updatedPlanet;
        if (updatedPlanetOptional.isEmpty())
            throw new PlanetNotFoundException();
        else
            updatedPlanet = updatedPlanetOptional.get();
        List<Planet> planets;
        try {
            planets = planetRepository.getAllPlanetsByGalaxyId(galaxyId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
        for (Planet currentPlanet : planets) {
            if (currentPlanet.getPlanetName().equals(updatedPlanet.getPlanetName()) && !currentPlanet.getPlanetId().equals(updatedPlanet.getPlanetId()))
                throw new PlanetAlreadyExistsException();
        }
        updatedPlanet.setPlanetName(planet.getPlanetName());
        updatedPlanet.setSystemId(planet.getSystemId());
        updatedPlanet.setPlanetNumber(planet.getPlanetNumber());
        return planetRepository.save(updatedPlanet);
    }

    private boolean checkSystemExistence(Integer systemId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            return restTemplate.exchange(GALAXY_APP_URL + GET_SYSTEM_BY_ID_URL + systemId, HttpMethod.GET, requestEntity, StarSystem.class).getStatusCode().equals(HttpStatus.OK);
        } catch (ResourceAccessException e) {
            throw new ConnectException();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }
}
