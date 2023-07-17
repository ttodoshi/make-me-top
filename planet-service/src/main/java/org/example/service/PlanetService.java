package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.coursetheme.CourseThemeCreateRequest;
import org.example.dto.coursetheme.CourseThemeDTO;
import org.example.dto.planet.CreatePlanet;
import org.example.dto.planet.PlanetUpdateRequest;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.Planet;
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

@Service
@RequiredArgsConstructor
@PropertySource(value = {"classpath:config.properties"})
public class PlanetService {
    private final PlanetRepository planetRepository;

    private final ModelMapper mapper;

    private final RestTemplate restTemplate;
    @Setter
    private String token;
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

    public List<Planet> getPlanetsListBySystemId(Integer systemId) {
        if (systemExists(systemId))
            return planetRepository.findPlanetsBySystemId(systemId);
        throw new SystemNotFoundException();
    }

    @Transactional
    public List<Planet> addPlanets(Integer systemId, List<CreatePlanet> planets) {
        List<CreatePlanet> savingPlanetsList = new LinkedList<>();
        for (CreatePlanet planet : planets) {
            if (!systemExists(systemId))
                throw new SystemNotFoundException();
            if (savingPlanetsList.contains(planet) || planetExists(systemId, planet.getPlanetName()))
                throw new PlanetAlreadyExistsException();
            savingPlanetsList.add(planet);
        }
        List<Planet> savedPlanets = new ArrayList<>();
        for (CreatePlanet currentPlanet : planets) {
            Planet planet = mapper.map(currentPlanet, Planet.class);
            planet.setSystemId(systemId);
            Planet savedPlanet = planetRepository.save(planet);
            savedPlanets.add(savedPlanet);
            addCourseTheme(systemId, savedPlanet.getPlanetId(), currentPlanet);
        }
        return savedPlanets;
    }

    private void addCourseTheme(Integer systemId, Integer courseThemeId, CreatePlanet planet) {
        HttpEntity<CourseThemeCreateRequest> entity = new HttpEntity<>(new CourseThemeCreateRequest(courseThemeId,
                planet.getPlanetName(), planet.getDescription(), planet.getContent(), planet.getPlanetNumber()),
                createHeaders());
        restTemplate.postForEntity(COURSE_APP_URL + "/course/" + systemId + "/theme", entity, CourseThemeDTO.class);
    }

    @Transactional
    public Planet updatePlanet(PlanetUpdateRequest planet, Integer planetId) {
        if (!systemExists(planet.getSystemId()))
            throw new SystemNotFoundException();
        Planet updatedPlanet = planetRepository.findById(planetId).orElseThrow(PlanetNotFoundException::new);
        if (planetExists(planet.getSystemId(), planet.getPlanetName()))
            throw new PlanetAlreadyExistsException();
        updatedPlanet.setPlanetName(planet.getPlanetName());
        updatedPlanet.setSystemId(planet.getSystemId());
        updatedPlanet.setPlanetNumber(planet.getPlanetNumber());
        return planetRepository.save(updatedPlanet);
    }

    private boolean planetExists(Integer systemId, String planetName) {
        return planetRepository.findPlanetsBySystemId(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName)
        );
    }

    public Map<String, String> deletePlanetById(Integer planetId) {
        if (!planetRepository.existsById(planetId))
            throw new PlanetNotFoundException();
        planetRepository.deleteById(planetId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
        return response;
    }

    private boolean systemExists(Integer systemId) {
        try {
            return restTemplate.exchange(
                            GALAXY_APP_URL + "/system/" + systemId,
                            HttpMethod.GET,
                            new HttpEntity<>(createHeaders()),
                            StarSystemDTO.class)
                    .getStatusCode().equals(HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return false;
        } catch (ResourceAccessException e) {
            throw new ConnectException();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }
}
