package org.example.service;

import org.example.dto.mmtr.MmtrAuthResponseEmployeeDto;
import org.example.grpc.PeopleService;

public interface PersonService {
    PeopleService.Person findPersonById(Integer personId);

    void savePersonIfNotExists(MmtrAuthResponseEmployeeDto authResponseEmployee);
}
