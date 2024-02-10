package org.example.planet.service;

public interface StarSystemService {
    Boolean existsById(String authorizationHeader, Long systemId);
}
