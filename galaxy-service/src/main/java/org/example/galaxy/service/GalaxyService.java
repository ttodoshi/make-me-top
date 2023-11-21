package org.example.galaxy.service;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.galaxy.CreateGalaxyDto;
import org.example.galaxy.dto.galaxy.GalaxyDto;
import org.example.galaxy.dto.galaxy.GetGalaxyDto;
import org.example.galaxy.dto.galaxy.GetGalaxyInformationDto;
import org.example.galaxy.dto.message.MessageDto;
import org.example.galaxy.dto.orbit.GetOrbitWithStarSystemsWithoutGalaxyIdDto;
import org.example.galaxy.dto.person.PersonWithSystemsDto;
import org.example.galaxy.exception.classes.galaxy.GalaxyNotFoundException;
import org.example.galaxy.exception.classes.system.SystemNotFoundException;
import org.example.galaxy.model.Galaxy;
import org.example.galaxy.model.StarSystem;
import org.example.galaxy.repository.GalaxyRepository;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
import org.example.galaxy.service.validator.GalaxyValidatorService;
import org.example.grpc.ExplorersService;
import org.example.grpc.KeepersService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final GalaxyValidatorService galaxyValidatorService;
    private final OrbitService orbitService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public GetGalaxyDto findGalaxyById(Integer galaxyId) {
        Galaxy galaxy = galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> new GalaxyNotFoundException(galaxyId));
        GetGalaxyDto galaxyWithOrbits = mapper.map(galaxy, GetGalaxyDto.class);
        galaxyWithOrbits.setOrbitList(
                orbitRepository.findOrbitsByGalaxyId(galaxyId)
                        .stream()
                        .map(o -> mapper.map(
                                        orbitService.findOrbitWithSystemList(
                                                o.getOrbitId()
                                        ),
                                        GetOrbitWithStarSystemsWithoutGalaxyIdDto.class
                                )
                        ).collect(Collectors.toList())
        );
        return galaxyWithOrbits;
    }

    @Transactional(readOnly = true)
    public Galaxy findGalaxyBySystemId(Integer systemId) {
        return galaxyRepository.findGalaxyBySystemId(systemId)
                .orElseThrow(() -> new SystemNotFoundException(systemId));
    }

    @Transactional(readOnly = true)
    public List<Galaxy> findAllGalaxies() {
        return galaxyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GetGalaxyInformationDto> findAllGalaxiesDetailed() {
        Map<Integer, ExplorersService.AllExplorersResponse.ExplorerList> explorers = explorerService.findExplorersWithCourseIds();
        Map<Integer, KeepersService.AllKeepersResponse.KeeperList> keepers = keeperService.findKeepersWithCourseIds();
        return galaxyRepository.findAll()
                .stream()
                .map(g -> {
                    List<StarSystem> systems = starSystemRepository.findStarSystemsByOrbit_GalaxyId(g.getGalaxyId());
                    List<PersonWithSystemsDto> personAsExplorerList = explorerService.getExplorersWithSystems(explorers, systems);
                    List<PersonWithSystemsDto> personAsKeeperList = keeperService.getKeepersWithSystems(keepers, systems);
                    return new GetGalaxyInformationDto(
                            g.getGalaxyId(),
                            g.getGalaxyName(),
                            g.getGalaxyDescription(),
                            systems.size(),
                            personAsExplorerList.size(),
                            personAsExplorerList,
                            personAsKeeperList.size(),
                            personAsKeeperList
                    );
                }).collect(Collectors.toList());
    }

    @Transactional
    public GetGalaxyDto createGalaxy(CreateGalaxyDto createGalaxyRequest) {
        galaxyValidatorService.validatePostRequest(createGalaxyRequest);
        Galaxy galaxy = mapper.map(createGalaxyRequest, Galaxy.class);
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        createGalaxyRequest.getOrbitList()
                .forEach(o -> orbitService.createOrbit(savedGalaxyId, o));
        return findGalaxyById(savedGalaxyId);
    }

    @Transactional
    public Galaxy updateGalaxy(Integer galaxyId, GalaxyDto galaxy) {
        galaxyValidatorService.validatePutRequest(galaxyId, galaxy);
        Galaxy updatedGalaxy = galaxyRepository.getReferenceById(galaxyId);
        updatedGalaxy.setGalaxyName(galaxy.getGalaxyName());
        updatedGalaxy.setGalaxyDescription(galaxy.getGalaxyDescription());
        return galaxyRepository.save(updatedGalaxy);
    }

    @Transactional
    public MessageDto deleteGalaxy(Integer galaxyId) {
        Galaxy galaxy = galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> new GalaxyNotFoundException(galaxyId));
        galaxy.getOrbits()
                .stream()
                .flatMap(g -> g.getSystems().stream())
                .forEach(s -> orbitService.clearCourseAndPlanets(s.getSystemId()));
        galaxyRepository.deleteById(galaxyId);
        return new MessageDto("Галактика " + galaxyId + " была уничтожена квазаром");
    }
}
