package org.example.auth.service;

import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.grpc.PeopleService;

public interface PersonService {
    PeopleService.Person findPersonById(Long personId);

    void savePerson(MmtrAuthResponseEmployeeDto authResponseEmployee);
}
