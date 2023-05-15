package org.example.sevice;

import org.example.exception.UserNotFoundException;
import org.example.model.Person;
import org.example.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class PersonService {


    @Autowired
    PersonRepository personRepository;

    Person person;

    public Person checkPersonById(Person person) {
        this.person = personRepository.getReferenceById(person.getPersonId());
        if (this.person == null) {
            person.setRole("explorer");
            personRepository.save(person);
            return person;
        } else {
            return this.person;
        }
    }

    public void updatePersonToCurator(Integer personId) {
        try {
            this.person = personRepository.getReferenceById(personId);
            this.person.setRole("keeper");
            personRepository.save(this.person);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException();
        }

    }
}
