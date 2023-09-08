package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.event.CourseThemeCreateEvent;
import org.example.dto.planet.CreatePlanetDto;
import org.example.dto.planet.UpdatePlanetDto;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.model.Planet;
import org.example.repository.PlanetRepository;
import org.example.service.validator.PlanetValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
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
    public List<Planet> addPlanets(Integer systemId, List<CreatePlanetDto> planets) {
        planetValidatorService.validatePostRequest(systemId, planets);
        List<Planet> savedPlanets = new ArrayList<>();
        for (CreatePlanetDto currentPlanet : planets) {
            Planet planet = mapper.map(currentPlanet, Planet.class);
            planet.setSystemId(systemId);
            Planet savedPlanet = planetRepository.save(planet);
            savedPlanets.add(savedPlanet);
            addCourseTheme(systemId, savedPlanet.getPlanetId(), currentPlanet);
        }
        return savedPlanets;
    }

    private void addCourseTheme(Integer systemId, Integer courseThemeId, CreatePlanetDto planet) {
        kafkaTemplate.send("courseThemeTopic", systemId, new CourseThemeCreateEvent(
                courseThemeId, planet.getPlanetName(),
                planet.getDescription(), planet.getContent(), planet.getPlanetNumber()));
    }

    @Transactional
    public Planet updatePlanet(Integer planetId, UpdatePlanetDto planet) {
        Planet updatedPlanet = planetRepository.findById(planetId)
                .orElseThrow(() -> new PlanetNotFoundException(planetId));
        planetValidatorService.validatePutRequest(planetId, planet);
        updatedPlanet.setPlanetName(planet.getPlanetName());
        updatedPlanet.setSystemId(planet.getSystemId());
        updatedPlanet.setPlanetNumber(planet.getPlanetNumber());
        return planetRepository.save(updatedPlanet);
    }

    @Transactional
    public Map<String, String> deletePlanetById(Integer planetId) {
        planetValidatorService.validateDeleteRequest(planetId);
        planetRepository.deleteById(planetId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
        return response;
    }

}