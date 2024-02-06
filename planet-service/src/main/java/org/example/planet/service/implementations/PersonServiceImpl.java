package org.example.planet.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.planet.exception.classes.person.PersonNotFoundException;
import org.example.grpc.PeopleService;
import org.example.planet.repository.PersonRepository;
import org.example.planet.service.PersonService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public PeopleService.Person findPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(PersonNotFoundException::new);
    }
}
