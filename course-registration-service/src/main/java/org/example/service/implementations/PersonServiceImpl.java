package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.grpc.PeopleService;
import org.example.repository.PersonRepository;
import org.example.service.PersonService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public Integer getAuthenticatedPersonId() {
        PeopleService.Person authenticatedPerson = (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    @Override
    public PeopleService.Person getAuthenticatedPerson() {
        return (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PeopleService.Person findPersonById(Integer personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }
}
