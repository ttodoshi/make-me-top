package org.example.config;


import org.example.model.Person;

public interface JwtGeneratorInterface {

    String generateToken(Person person);
}
