package org.example.service;

import org.example.dto.PersonDto;
import org.example.dto.mmtr.MmtrAuthResponseEmployeeDto;

public interface PersonService {
    PersonDto findPersonById(Integer personId);

    void savePersonIfNotExists(MmtrAuthResponseEmployeeDto authResponseEmployee);
}
