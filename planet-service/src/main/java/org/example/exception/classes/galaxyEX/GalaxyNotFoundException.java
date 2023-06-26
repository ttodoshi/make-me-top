package org.example.exception.classes.galaxyEX;

import javax.persistence.EntityNotFoundException;

public class GalaxyNotFoundException extends EntityNotFoundException {
    public GalaxyNotFoundException() {
        super("Не удалось найти информацию о данной галактике в памяти бортового компьютера");
    }
}

