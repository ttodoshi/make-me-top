package org.example.galaxy.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.exception.classes.person.PersonNotFoundException;
import org.example.galaxy.repository.PersonRepository;
import org.example.galaxy.service.PersonService;
import org.example.grpc.PeopleService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PeopleService.Person findPersonById(Integer personId) {
        return personRepository.findById(personId)
                .orElseThrow(PersonNotFoundException::new);
    }
}
