package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.progress.exception.classes.person.PersonNotFoundException;
import org.example.grpc.PeopleService;
import org.example.progress.repository.PersonRepository;
import org.example.progress.service.PersonService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public Long getAuthenticatedPersonId() {
        PeopleService.Person authenticatedPerson = (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    @Override
    public PeopleService.Person getAuthenticatedPerson() {
        return (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PeopleService.Person findPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }
}
