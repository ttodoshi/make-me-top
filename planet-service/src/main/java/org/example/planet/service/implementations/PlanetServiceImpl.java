package org.example.planet.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.planet.dto.event.PlanetUpdateEvent;
import org.example.planet.dto.message.MessageDto;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.PlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.example.planet.exception.planet.PlanetNotFoundException;
import org.example.planet.model.Planet;
import org.example.planet.repository.PlanetRepository;
import org.example.planet.service.PlanetService;
import org.example.planet.service.validator.PlanetValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanetServiceImpl implements PlanetService {
    private final PlanetRepository planetRepository;

    private final PlanetValidatorService planetValidatorService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PlanetDto findPlanetById(Long planetId) {
        return planetRepository.findById(planetId)
                .map(p -> mapper.map(p, PlanetDto.class))
                .orElseThrow(() -> {
                    log.warn("planet by id {} not found", planetId);
                    return new PlanetNotFoundException(planetId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanetDto> findPlanetsBySystemId(String authorizationHeader, Long systemId) {
        planetValidatorService.validateGetPlanetsRequest(authorizationHeader, systemId);

        return planetRepository
                .findPlanetsBySystemIdOrderByPlanetNumber(systemId)
                .stream()
                .map(p -> mapper.map(p, PlanetDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, PlanetDto> findPlanetsByPlanetIdIn(List<Long> planetIds) {
        return planetRepository.findPlanetsByPlanetIdIn(planetIds)
                .stream()
                .collect(Collectors.toMap(
                        Planet::getPlanetId,
                        p -> mapper.map(p, PlanetDto.class)
                ));
    }

    @Override
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

    @Override
    @Transactional
    public List<Long> createPlanets(String authorizationHeader, Long systemId, List<CreatePlanetDto> planets) {
        planetValidatorService.validatePostRequest(authorizationHeader, systemId, planets);

        return planets.stream()
                .map(p -> {
                    Planet planet = mapper.map(p, Planet.class);
                    planet.setSystemId(systemId);
                    return planetRepository
                            .save(planet)
                            .getPlanetId();
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlanetDto updatePlanet(String authorizationHeader, Long planetId, UpdatePlanetDto planet) {
        Planet updatedPlanet = planetRepository.findById(planetId)
                .orElseThrow(() -> {
                    log.warn("planet by id {} not found", planetId);
                    return new PlanetNotFoundException(planetId);
                });

        planetValidatorService.validatePutRequest(authorizationHeader, planetId, planet);

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
    public void updatePlanetListener(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Long planetId,
                                     @Payload PlanetUpdateEvent planetRequest) {
        Planet planet = planetRepository.findById(planetId)
                .orElseThrow(() -> {
                    log.warn("planet by id {} not found", planetId);
                    return new PlanetNotFoundException(planetId);
                });

        planet.setPlanetName(planetRequest.getPlanetName());
        planet.setPlanetNumber(planetRequest.getPlanetNumber());
        planet.setSystemId(planetRequest.getSystemId());
        planetRepository.save(planet);
    }

    @Override
    @Transactional
    public MessageDto deletePlanetById(Long planetId) {
        planetValidatorService.validateDeleteRequest(planetId);
        planetRepository.deleteById(planetId);
        return new MessageDto(
                String.format("Планета %d подлежит уничтожению для создания межгалактической трассы", planetId)
        );
    }

    @KafkaListener(topics = "deletePlanetsTopic", containerFactory = "deletePlanetsKafkaListenerContainerFactory")
    @Transactional
    public void deletePlanets(Long systemId) {
        planetRepository.deletePlanetsBySystemId(systemId);
    }
}