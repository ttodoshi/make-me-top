package org.example.picture.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.grpc.PeopleService;
import org.example.picture.exception.classes.person.PersonNotFoundException;
import org.example.picture.repository.PersonRepository;
import org.example.picture.service.PersonService;
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
