package org.example.planet.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.course.dto.event.CourseThemeCreateEvent;
import org.example.planet.dto.message.MessageDto;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.example.planet.exception.classes.planet.PlanetNotFoundException;
import org.example.planet.model.Planet;
import org.example.planet.service.validator.PlanetValidatorService;
import org.example.planet.repository.PlanetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanetService {
    private final PlanetRepository planetRepository;

    private final PlanetValidatorService planetValidatorService;
    private final ModelMapper mapper;
    private final KafkaTemplate<Integer, Object> createThemeKafkaTemplate;
    private final KafkaTemplate<Integer, String> updateThemeKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteThemeKafkaTemplate;

    public Planet findPlanetById(Integer planetId) {
        return planetRepository.findById(planetId)
                .orElseThrow(() -> new PlanetNotFoundException(planetId));
    }

    @Transactional(readOnly = true)
    public List<Planet> findPlanetsBySystemId(Integer systemId) {
        planetValidatorService.validateGetPlanetsRequest(systemId);
        return planetRepository.findPlanetsBySystemIdOrderByPlanetNumber(systemId);
    }

    public Map<Integer, Planet> findPlanetsByPlanetIdIn(List<Integer> planetIds) {
        return planetRepository.findPlanetsByPlanetIdIn(planetIds)
                .stream()
                .collect(Collectors.toMap(
                        Planet::getPlanetId,
                        p -> p
                ));
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<Planet>> findPlanetsBySystemIdIn(List<Integer> systemIds) {
        return planetRepository.findPlanetsBySystemIdIn(systemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        Planet::getSystemId,
                        Collectors.mapping(
                                p -> p,
                                Collectors.toList()
                        )
                ));
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
            createCourseTheme(systemId, savedPlanet.getPlanetId(), currentPlanet);
        }
        return savedPlanets;
    }

    private void createCourseTheme(Integer systemId, Integer courseThemeId, CreatePlanetDto planet) {
        createThemeKafkaTemplate.send("createCourseThemeTopic", systemId, new CourseThemeCreateEvent(
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
        updateCourseThemeTitle(planetId, planet.getPlanetName());
        return planetRepository.save(updatedPlanet);
    }

    @KafkaListener(topics = "updatePlanetTopic", containerFactory = "updatePlanetKafkaListenerContainerFactory")
    @Transactional
    public void updatePlanetName(ConsumerRecord<Integer, String> record) {
        Planet planet = planetRepository.findById(record.key())
                .orElseThrow(() -> new PlanetNotFoundException(record.key()));
        planet.setPlanetName(record.value());
        planetRepository.save(planet);
    }

    private void updateCourseThemeTitle(Integer planetId, String planetName) {
        updateThemeKafkaTemplate.send("updateCourseThemeTopic", planetId, planetName);
    }

    @Transactional
    public MessageDto deletePlanetById(Integer planetId) {
        planetValidatorService.validateDeleteRequest(planetId);
        planetRepository.deleteById(planetId);
        deleteCourseTheme(planetId);
        return new MessageDto("Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
    }

    @KafkaListener(topics = "deletePlanetsTopic", containerFactory = "deletePlanetsKafkaListenerContainerFactory")
    @Transactional
    public void deletePlanets(Integer systemId) {
        planetRepository.deletePlanetsBySystemId(systemId);
    }

    private void deleteCourseTheme(Integer planetId) {
        deleteThemeKafkaTemplate.send("deleteCourseThemeTopic", planetId);
    }
}