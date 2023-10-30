package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.PersonDto;
import org.example.exception.classes.personEX.PersonNotFoundException;
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
        PersonDto authenticatedPerson = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PersonDto findPersonById(Integer personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
    }
}
