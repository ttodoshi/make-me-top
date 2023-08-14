package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.planet.PlanetCreateRequest;
import org.example.dto.planet.PlanetUpdateRequest;
import org.example.dto.event.CourseThemeCreateEvent;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.model.Planet;
import org.example.repository.PlanetRepository;
import org.example.service.validator.PlanetValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@PropertySource(value = {"classpath:config.properties"})
public class PlanetService {
    private final PlanetRepository planetRepository;

    private final PlanetValidatorService planetValidatorService;
    private final ModelMapper mapper;
    private final KafkaTemplate<Integer, Object> kafkaTemplate;

    public List<Planet> getPlanetsListBySystemId(Integer systemId) {
        planetValidatorService.validateGetPlanetsRequest(systemId);
        return planetRepository.findPlanetsBySystemId(systemId);
    }

    @Transactional
    public List<Planet> addPlanets(Integer systemId, List<PlanetCreateRequest> planets) {
        planetValidatorService.validatePostRequest(systemId, planets);
        List<Planet> savedPlanets = new ArrayList<>();
        for (PlanetCreateRequest currentPlanet : planets) {
            Planet planet = mapper.map(currentPlanet, Planet.class);
            planet.setSystemId(systemId);
            Planet savedPlanet = planetRepository.save(planet);
            savedPlanets.add(savedPlanet);
            addCourseTheme(systemId, savedPlanet.getPlanetId(), currentPlanet);
        }
        return savedPlanets;
    }

    private void addCourseTheme(Integer systemId, Integer courseThemeId, PlanetCreateRequest planet) {
        kafkaTemplate.send("courseThemeTopic", systemId, new CourseThemeCreateEvent(
                courseThemeId, planet.getPlanetName(),
                planet.getDescription(), planet.getContent(), planet.getPlanetNumber()));
    }

    @Transactional
    public Planet updatePlanet(Integer planetId, PlanetUpdateRequest planet) {
        Planet updatedPlanet = planetRepository.findById(planetId)
                .orElseThrow(() -> new PlanetNotFoundException(planetId));
        planetValidatorService.validatePutRequest(planetId, planet);
        updatedPlanet.setPlanetName(planet.getPlanetName());
        updatedPlanet.setSystemId(planet.getSystemId());
        updatedPlanet.setPlanetNumber(planet.getPlanetNumber());
        return planetRepository.save(updatedPlanet);
    }

    public Map<String, String> deletePlanetById(Integer planetId) {
        planetValidatorService.validateDeleteRequest(planetId);
        planetRepository.deleteById(planetId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
        return response;
    }

}