package org.example.service;

import org.example.dto.mmtr.MmtrAuthResponseEmployeeDto;

public interface PersonService {
    void savePersonIfNotExists(MmtrAuthResponseEmployeeDto authResponseEmployee);
}
