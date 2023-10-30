package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.galaxy.CreateGalaxyDto;
import org.example.dto.galaxy.GalaxyDto;
import org.example.dto.galaxy.GetGalaxyDto;
import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.dto.keeper.KeeperDto;
import org.example.dto.message.MessageDto;
import org.example.dto.orbit.GetOrbitWithStarSystemsWithoutGalaxyIdDto;
import org.example.dto.person.PersonWithSystemsDto;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.Galaxy;
import org.example.model.StarSystem;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.example.service.validator.GalaxyValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
    public List<Galaxy> getAllGalaxies() {
        return galaxyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GetGalaxyInformationDto> getAllGalaxiesDetailed() {
        Map<Integer, List<ExplorerDto>> explorers = explorerService.findExplorersWithCourseIds();
        Map<Integer, List<KeeperDto>> keepers = keeperService.findKeepersWithCourseIds();
        return galaxyRepository.findAll()
                .stream()
                .map(g -> {
                    List<StarSystem> systems = starSystemRepository.findSystemsByGalaxyId(g.getGalaxyId());
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

    @Transactional(readOnly = true)
    public GetGalaxyDto getGalaxyById(Integer galaxyId) {
        galaxyValidatorService.validateGetByIdRequest(galaxyId);
        GetGalaxyDto galaxy = mapper.map(galaxyRepository.getReferenceById(galaxyId), GetGalaxyDto.class);
        List<GetOrbitWithStarSystemsWithoutGalaxyIdDto> orbitWithStarSystemsList = new ArrayList<>();
        orbitRepository.findOrbitsByGalaxyId(galaxyId).forEach(
                o -> orbitWithStarSystemsList.add(
                        mapper.map(
                                orbitService.getOrbitWithSystemList(o.getOrbitId()),
                                GetOrbitWithStarSystemsWithoutGalaxyIdDto.class)
                )
        );
        galaxy.setOrbitList(orbitWithStarSystemsList);
        return galaxy;
    }

    @Transactional(readOnly = true)
    public Galaxy getGalaxyBySystemId(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
        Integer galaxyId = galaxyRepository.getGalaxyIdBySystemId(systemId);
        return galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> new GalaxyNotFoundException(galaxyId));
    }

    @Transactional
    public GetGalaxyDto createGalaxy(CreateGalaxyDto createGalaxyDto) {
        galaxyValidatorService.validatePostRequest(createGalaxyDto);
        Galaxy galaxy = mapper.map(createGalaxyDto, Galaxy.class);
        Integer savedGalaxyId = galaxyRepository.save(galaxy).getGalaxyId();
        createGalaxyDto.getOrbitList().forEach(o -> orbitService.createOrbit(savedGalaxyId, o));
        return getGalaxyById(savedGalaxyId);
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
        galaxyValidatorService.validateDeleteRequest(galaxyId);
        galaxyRepository.deleteById(galaxyId);
        return new MessageDto("Галактика " + galaxyId + " была уничтожена квазаром");
    }
}
