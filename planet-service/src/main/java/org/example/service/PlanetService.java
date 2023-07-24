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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@PropertySource(value = {"classpath:config.properties"})
public class PlanetService {
    private final PlanetRepository planetRepository;

    private final ModelMapper mapper;

    @Setter
    private String token;
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;
    @Value("${course_app_url}")
    private String COURSE_APP_URL;

    public List<Planet> getPlanetsListBySystemId(Integer systemId) {
        checkSystemExists(systemId);
        return planetRepository.findPlanetsBySystemId(systemId);
    }

    @Transactional
    public List<Planet> addPlanets(Integer systemId, List<CreatePlanet> planets) {
        checkSystemExists(systemId);
        List<CreatePlanet> savingPlanetsList = new LinkedList<>();
        for (CreatePlanet planet : planets) {
            if (savingPlanetsList.contains(planet) || planetExists(systemId, planet.getPlanetName()))
                throw new PlanetAlreadyExistsException(planet.getPlanetName());
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
        WebClient webClient = WebClient.create(COURSE_APP_URL);
        webClient.post()
                .uri("/course/" + systemId + "/theme")
                .bodyValue(new CourseThemeCreateRequest(courseThemeId, planet.getPlanetName(),
                        planet.getDescription(), planet.getContent(), planet.getPlanetNumber()))
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(CourseThemeDTO.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> {
                    throw new ConnectException();
                })
                .subscribe();
    }

    @Transactional
    public Planet updatePlanet(PlanetUpdateRequest planet, Integer planetId) {
        checkSystemExists(planet.getSystemId());
        Planet updatedPlanet = planetRepository.findById(planetId)
                .orElseThrow(() -> new PlanetNotFoundException(planetId));
        if (planetExists(planet.getSystemId(), planet.getPlanetName()))
            throw new PlanetAlreadyExistsException(planet.getPlanetName());
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
            throw new PlanetNotFoundException(planetId);
        planetRepository.deleteById(planetId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
        return response;
    }

    private void checkSystemExists(Integer systemId) {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        webClient.get()
                .uri("/system/" + systemId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new SystemNotFoundException(systemId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}