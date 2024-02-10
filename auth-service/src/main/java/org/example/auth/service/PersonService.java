package org.example.auth.service;

import org.example.auth.dto.mmtr.MmtrAuthResponseEmployeeDto;

public interface PersonService {
    void savePerson(MmtrAuthResponseEmployeeDto authResponseEmployee);
}
