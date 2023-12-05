package org.example.planet.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.planet.dto.message.MessageDto;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.PlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.example.planet.exception.classes.planet.PlanetNotFoundException;
import org.example.planet.model.Planet;
import org.example.planet.repository.PlanetRepository;
import org.example.planet.service.validator.PlanetValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanetService {
    private final PlanetRepository planetRepository;

    private final PlanetValidatorService planetValidatorService;
    private final ModelMapper mapper;

    public PlanetDto findPlanetById(Long planetId) {
        return planetRepository.findById(planetId)
                .map(p -> mapper.map(p, PlanetDto.class))
                .orElseThrow(() -> new PlanetNotFoundException(planetId));
    }

    @Transactional(readOnly = true)
    public List<PlanetDto> findPlanetsBySystemId(Long systemId) {
        planetValidatorService.validateGetPlanetsRequest(systemId);

        return planetRepository
                .findPlanetsBySystemIdOrderByPlanetNumber(systemId)
                .stream()
                .map(p -> mapper.map(p, PlanetDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<Long, PlanetDto> findPlanetsByPlanetIdIn(List<Long> planetIds) {
        return planetRepository.findPlanetsByPlanetIdIn(planetIds)
                .stream()
                .collect(Collectors.toMap(
                        Planet::getPlanetId,
                        p -> mapper.map(p, PlanetDto.class)
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, List<PlanetDto>> findPlanetsBySystemIdIn(List<Long> systemIds) {
        return planetRepository.findPlanetsBySystemIdIn(systemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        Planet::getSystemId,
                        Collectors.mapping(
                                p -> mapper.map(p, PlanetDto.class),
                                Collectors.toList()
                        )
                ));
    }

    @Transactional
    public List<PlanetDto> createPlanets(Long systemId, List<CreatePlanetDto> planets) {
        planetValidatorService.validatePostRequest(systemId, planets);

        return planets.stream()
                .map(p -> {
                    Planet planet = mapper.map(p, Planet.class);
                    planet.setSystemId(systemId);
                    return mapper.map(
                            planetRepository.save(planet),
                            PlanetDto.class
                    );
                }).collect(Collectors.toList());
    }

    @Transactional
    public PlanetDto updatePlanet(Long planetId, UpdatePlanetDto planet) {
        Planet updatedPlanet = planetRepository.findById(planetId)
                .orElseThrow(() -> new PlanetNotFoundException(planetId));

        planetValidatorService.validatePutRequest(planetId, planet);

        updatedPlanet.setPlanetName(planet.getPlanetName());
        updatedPlanet.setSystemId(planet.getSystemId());
        updatedPlanet.setPlanetNumber(planet.getPlanetNumber());

        return mapper.map(
                planetRepository.save(updatedPlanet),
                PlanetDto.class
        );
    }

    @KafkaListener(topics = "updatePlanetTopic", containerFactory = "updatePlanetKafkaListenerContainerFactory")
    @Transactional
    public void updatePlanetName(ConsumerRecord<Long, String> record) {
        Planet planet = planetRepository.findById(record.key())
                .orElseThrow(() -> new PlanetNotFoundException(record.key()));
        planet.setPlanetName(record.value());

        planetRepository.save(planet);
    }

    @Transactional
    public MessageDto deletePlanetById(Long planetId) {
        planetValidatorService.validateDeleteRequest(planetId);
        planetRepository.deleteById(planetId);
        return new MessageDto("Планета " + planetId + " подлежит уничтожению для создания межгалактической трассы");
    }

    @KafkaListener(topics = "deletePlanetsTopic", containerFactory = "deletePlanetsKafkaListenerContainerFactory")
    @Transactional
    public void deletePlanets(Long systemId) {
        planetRepository.deletePlanetsBySystemId(systemId);
    }
}