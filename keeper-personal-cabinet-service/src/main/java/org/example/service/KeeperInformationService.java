package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeeperInformationService {
    private final PersonRepository personRepository;
}
