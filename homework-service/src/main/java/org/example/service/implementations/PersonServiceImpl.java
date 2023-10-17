package org.example.service.implementations;

import org.example.dto.person.PersonDto;
import org.example.service.PersonService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PersonServiceImpl implements PersonService {
    @Override
    public Integer getAuthenticatedPersonId() {
        PersonDto authenticatedPerson = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }
}
