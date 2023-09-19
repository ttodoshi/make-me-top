package org.example.service;

import org.example.dto.AuthResponseEmployeeDto;

public interface PersonService {
    void savePersonIfNotExists(AuthResponseEmployeeDto authResponseEmployee);
}
