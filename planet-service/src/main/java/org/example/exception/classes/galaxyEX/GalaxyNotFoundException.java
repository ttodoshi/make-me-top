package org.example.exception.classes.galaxyEX;

import javax.persistence.EntityNotFoundException;

public class GalaxyNotFoundException extends EntityNotFoundException {
    public GalaxyNotFoundException() {
        super("Галактика не найдена");
    }
}

