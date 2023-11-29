package org.example.planet.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.course.dto.event.CourseThemeCreateEvent;
import org.example.planet.dto.message.MessageDto;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.example.planet.exception.classes.planet.PlanetNotFoundException;
import org.example.planet.model.Planet;
import org.example.planet.repository.PlanetRepository;
import org.example.planet.service.validator.PlanetValidatorService;
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
    private final KafkaTemplate<Long, Object> createThemeKafkaTemplate;
    private final KafkaTemplate<Long, String> updateThemeKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteThemeKafkaTemplate;

    public Planet findPlanetById(Long planetId) {
        return planetRepository.findById(planetId)
                .orElseThrow(() -> new PlanetNotFoundException(planetId));
    }

    @Transactional(readOnly = true)
    public List<Planet> findPlanetsBySystemId(Long systemId) {
        planetValidatorService.validateGetPlanetsRequest(systemId);
        return planetRepository.findPlanetsBySystemIdOrderByPlanetNumber(systemId);
    }

    public Map<Long, Planet> findPlanetsByPlanetIdIn(List<Long> planetIds) {
        return planetRepository.findPlanetsByPlanetIdIn(planetIds)
                .stream()
                .collect(Collectors.toMap(
                        Planet::getPlanetId,
                        p -> p
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, List<Planet>> findPlanetsBySystemIdIn(List<Long> systemIds) {
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
    public List<Planet> addPlanets(Long systemId, List<CreatePlanetDto> planets) {
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

    private void createCourseTheme(Long systemId, Long courseThemeId, CreatePlanetDto planet) {
        createThemeKafkaTemplate.send("createCourseThemeTopic", systemId, new CourseThemeCreateEvent(
                courseThemeId, planet.getPlanetName(),
                planet.getDescription(), planet.getContent(), planet.getPlanetNumber()));
    }

    @Transactional
    public Planet updatePlanet(Long planetId, UpdatePlanetDto planet) {
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
    public void updatePlanetName(ConsumerRecord<Long, String> record) {
        Planet planet = planetRepository.findById(record.key())
                .orElseThrow(() -> new PlanetNotFoundException(record.key()));
        planet.setPlanetName(record.value());
        planetRepository.save(planet);
    }

    private void updateCourseThemeTitle(Long planetId, String planetName) {
        updateThemeKafkaTemplate.send("updateCourseThemeTopic", planetId, planetName);
    }

    @Transactional
    public MessageDto deletePlanetById(Long planetId) {
        planetValidatorService.validateDeleteRequest(planetId);
        planetRepository.deleteById(planetId);
        deleteCourseTheme(planetId);
        return new MessageDto("Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
    }

    @KafkaListener(topics = "deletePlanetsTopic", containerFactory = "deletePlanetsKafkaListenerContainerFactory")
    @Transactional
    public void deletePlanets(Long systemId) {
        planetRepository.deletePlanetsBySystemId(systemId);
    }

    private void deleteCourseTheme(Long planetId) {
        deleteThemeKafkaTemplate.send("deleteCourseThemeTopic", planetId);
    }
}