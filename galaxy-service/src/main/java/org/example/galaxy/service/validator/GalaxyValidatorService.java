package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxy.dto.galaxy.CreateGalaxyDto;
import org.example.galaxy.dto.galaxy.UpdateGalaxyDto;
import org.example.galaxy.exception.galaxy.GalaxyAlreadyExistsException;
import org.example.galaxy.exception.galaxy.GalaxyNotFoundException;
import org.example.galaxy.repository.GalaxyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GalaxyValidatorService {
    private final GalaxyRepository galaxyRepository;

    @Transactional(readOnly = true)
    public void validatePostRequest(CreateGalaxyDto galaxy) {
        if (galaxyRepository.existsGalaxyByGalaxyName(galaxy.getGalaxyName())) {
            log.warn("galaxy '{}' already exists", galaxy.getGalaxyName());
            throw new GalaxyAlreadyExistsException(galaxy.getGalaxyName());
        }
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Long galaxyId, UpdateGalaxyDto galaxy) {
        if (galaxyRepository.existsGalaxyByGalaxyIdNotAndGalaxyName(
                galaxyId, galaxy.getGalaxyName()
        )) {
            log.warn("galaxy '{}' already exists", galaxy.getGalaxyName());
            throw new GalaxyAlreadyExistsException(galaxy.getGalaxyName());
        }
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Long galaxyId) {
        if (!galaxyRepository.existsById(galaxyId)) {
            log.warn("galaxy by id {} not found", galaxyId);
            throw new GalaxyNotFoundException(galaxyId);
        }
    }
}
