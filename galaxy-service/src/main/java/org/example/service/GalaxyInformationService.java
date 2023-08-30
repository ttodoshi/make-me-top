package org.example.service;

import org.example.dto.galaxy.GalaxyInformationGetResponse;
import org.example.model.Galaxy;

public interface GalaxyInformationService {
    GalaxyInformationGetResponse getGalaxyInformation(Galaxy galaxy);
}
