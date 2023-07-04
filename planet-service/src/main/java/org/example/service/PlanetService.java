package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.coursetheme.CourseThemeCreateRequest;
import org.example.dto.coursetheme.CourseThemeDTO;
import org.example.dto.planet.PlanetDTO;
import org.example.dto.planet.PlanetUpdateRequest;
import org.example.exception.classes.connectEX.ConnectException;
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
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

    public List<Planet> getPlanetsListBySystemId(Integer systemId) {
        if (doesSystemExist(systemId))
            return planetRepository.getPlanetsBySystemId(systemId);
        throw new SystemNotFoundException();
    }

    @Transactional
    public List<Planet> addPlanet(List<PlanetDTO> planets) {
        List<Planet> savedPlanets = new LinkedList<>();
        for (PlanetDTO planet : planets) {
            if (!doesSystemExist(planet.getSystemId()))
                throw new SystemNotFoundException();
            else if (planetRepository.getPlanetsBySystemId(planet.getSystemId())
                    .stream().noneMatch(
                            x -> Objects.equals(x.getPlanetName(), planet.getPlanetName()))) {
                Planet savedPlanet = planetRepository.save(mapper.map(planet, Planet.class));
                addCourseTheme(savedPlanet.getPlanetId(), planet);
                savedPlanets.add(savedPlanet);
            } else
                throw new PlanetAlreadyExistsException();
        }
        return savedPlanets;
    }

    private void addCourseTheme(Integer courseThemeId, PlanetDTO planet) {
        HttpEntity<CourseThemeCreateRequest> entity = new HttpEntity<>(new CourseThemeCreateRequest(courseThemeId,
                planet.getPlanetName(), planet.getDescription(), planet.getContent()),
                createHeaders());
        restTemplate.postForEntity(COURSE_APP_URL + "/course/" + planet.getSystemId() + "/theme", entity, CourseThemeDTO.class);
    }

    @Transactional
    public Planet updatePlanet(PlanetUpdateRequest planet, Integer planetId) {
        if (!doesSystemExist(planet.getSystemId()))
            throw new SystemNotFoundException();
        Optional<Planet> updatedPlanetOptional = planetRepository.findById(planetId);
        Planet updatedPlanet;
        if (updatedPlanetOptional.isEmpty())
            throw new PlanetNotFoundException();
        else
            updatedPlanet = updatedPlanetOptional.get();
        for (Planet currentPlanet : planetRepository.getPlanetsBySystemId(planet.getSystemId())) {
            if (currentPlanet.getPlanetName().equals(updatedPlanet.getPlanetName()) && !currentPlanet.getPlanetId().equals(updatedPlanet.getPlanetId()))
                throw new PlanetAlreadyExistsException();
        }
        updatedPlanet.setPlanetName(planet.getPlanetName());
        updatedPlanet.setSystemId(planet.getSystemId());
        updatedPlanet.setPlanetNumber(planet.getPlanetNumber());
        return planetRepository.save(updatedPlanet);
    }

    @Transactional
    public Map<String, String> deletePlanetById(Integer planetId) {
        try {
            planetRepository.deleteById(planetId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new PlanetNotFoundException();
        }
    }

    private boolean doesSystemExist(Integer systemId) {
        try {
            return restTemplate.exchange(
                            GALAXY_APP_URL + "/system/" + systemId,
                            HttpMethod.GET,
                            new HttpEntity<>(createHeaders()),
                            StarSystem.class)
                    .getStatusCode().equals(HttpStatus.OK);
        } catch (ResourceAccessException e) {
            throw new ConnectException();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }
}
