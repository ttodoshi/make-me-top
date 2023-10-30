package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.person.PersonDto;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.repository.PersonRepository;
import org.example.service.PersonService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    @Cacheable(cacheNames = "personByIdCache", key = "#personId")
    public PersonDto findPersonById(Integer personId) {
        return personRepository.findById(personId)
                .orElseThrow(PersonNotFoundException::new);
    }
}
