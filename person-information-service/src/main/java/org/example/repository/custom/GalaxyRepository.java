package org.example.repository.custom;

import org.example.dto.galaxy.GalaxyDTO;
import org.example.dto.galaxy.GalaxyInformationGetResponse;

public interface GalaxyRepository {
    GalaxyInformationGetResponse[] getGalaxies();

    GalaxyDTO getGalaxyBySystemId(Integer systemId);
}
