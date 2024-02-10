package org.example.galaxy.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxy.dto.galaxy.*;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.exception.galaxy.GalaxyNotFoundException;
import org.example.galaxy.exception.system.SystemNotFoundException;
import org.example.galaxy.model.Galaxy;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.repository.GalaxyRepository;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
import org.example.galaxy.service.ExplorerService;
import org.example.galaxy.service.GalaxyService;
import org.example.galaxy.service.KeeperService;
import org.example.galaxy.service.OrbitService;
import org.example.galaxy.service.validator.GalaxyValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GalaxyServiceImpl implements GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final GalaxyValidatorService galaxyValidatorService;
    private final OrbitService orbitService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public GetGalaxyDto findGalaxyById(Long galaxyId) {
        Galaxy galaxy = galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> {
                    log.warn("galaxy by id {} not found", galaxyId);
                    return new GalaxyNotFoundException(galaxyId);
                });

        GetGalaxyDto galaxyWithOrbits = mapper.map(galaxy, GetGalaxyDto.class);
        galaxyWithOrbits.setOrbitList(
                orbitRepository.findOrbitsByGalaxyIdOrderByOrbitLevel(galaxyId)
                        .stream()
                        .map(o -> orbitService.findOrbitWithSystemList(
                                o.getOrbitId()
                        )).collect(Collectors.toList())
        );
        return galaxyWithOrbits;
    }

    @Override
    @Transactional(readOnly = true)
    public GetGalaxyInformationDto findGalaxyByIdDetailed(Long galaxyId) {
        Galaxy galaxy = galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> {
                    log.warn("galaxy by id {} not found", galaxyId);
                    return new GalaxyNotFoundException(galaxyId);
                });

        List<StarSystem> systems = galaxy.getOrbits().stream()
                .flatMap(o -> o.getSystems().stream())
                .collect(Collectors.toList());

        List<PersonWithSystemsDto> personAsExplorerList = explorerService
                .getExplorersWithSystems(systems);
        List<PersonWithSystemsDto> personAsKeeperList = keeperService
                .getKeepersWithSystems(systems);

        return new GetGalaxyInformationDto(
                galaxy.getGalaxyId(), galaxy.getGalaxyName(),
                galaxy.getGalaxyDescription(), systems.size(),
                personAsExplorerList.size(), personAsExplorerList,
                personAsKeeperList.size(), personAsKeeperList
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalaxyDto> findAllGalaxies() {
        return galaxyRepository.findAll()
                .stream()
                .map(g -> mapper.map(g, GalaxyDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GalaxyDto findGalaxyBySystemId(Long systemId) {
        return mapper.map(
                starSystemRepository.findById(systemId)
                        .orElseThrow(() -> new SystemNotFoundException(systemId))
                        .getOrbit()
                        .getGalaxy(),
                GalaxyDto.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, GalaxyDto> findGalaxyBySystemIdIn(List<Long> systemIds) {
        return starSystemRepository.findStarSystemsBySystemIdIn(systemIds)
                .stream()
                .collect(Collectors.toMap(
                        StarSystem::getSystemId,
                        s -> mapper.map(
                                s.getOrbit().getGalaxy(),
                                GalaxyDto.class
                        )
                ));
    }

    @Override
    @Transactional
    public Long createGalaxy(CreateGalaxyDto createGalaxyRequest) {
        galaxyValidatorService.validatePostRequest(createGalaxyRequest);

        Galaxy galaxy = mapper.map(createGalaxyRequest, Galaxy.class);
        Long savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();

        createGalaxyRequest.getOrbitList()
                .forEach(o -> orbitService.createOrbit(savedGalaxyId, o));

        return savedGalaxyId;
    }

    @Override
    @Transactional
    public GalaxyDto updateGalaxy(Long galaxyId, UpdateGalaxyDto galaxy) {
        Galaxy updatedGalaxy = galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> {
                    log.warn("galaxy by id {} not found", galaxyId);
                    return new GalaxyNotFoundException(galaxyId);
                });

        galaxyValidatorService.validatePutRequest(galaxyId, galaxy);

        updatedGalaxy.setGalaxyName(galaxy.getGalaxyName());
        updatedGalaxy.setGalaxyDescription(galaxy.getGalaxyDescription());

        return mapper.map(
                galaxyRepository.save(updatedGalaxy),
                GalaxyDto.class
        );
    }

    @Override
    @Transactional
    public MessageDto deleteGalaxy(Long galaxyId) {
        galaxyValidatorService.validateDeleteRequest(galaxyId);
        galaxyRepository.deleteById(galaxyId);
        return new MessageDto("Галактика " + galaxyId + " была уничтожена квазаром");
    }
}
